import numpy as np
import keras
import time
import os


class Network(object):
    def __init__(self, trainingDataGS, trainingDataInstr):

        self.trainingDataGS = trainingDataGS   # open("trainingDataGS.csv", "a+")
        self.trainingDataInstr = trainingDataInstr     # open("trainingDataInstr.csv", "a+")
        self.fileOpened = False

        # Array of game history
        self.GameFrames = np.zeros([3200, 271])
        # Array of action-mapped responses
        self.ActionMaps = np.zeros([3200, 5, 9, 16])
        self.frameCnt = 0
        # Reward map
        self.rewards = np.zeros([3200, 5, 9, 16])
        # Network Model
        self.Model = self.model()

    def newGame(self):
        # Array of game history
        self.GameFrames = np.zeros([3200, 271])
        # Array of action-mapped responses
        self.ActionMaps = np.zeros([3200, 5, 9, 16])
        self.frameCnt = 0
        # Reward map
        self.rewards = np.zeros([3200, 5, 9, 16])

    def openFiles(self):
        millis = int(round(time.time() * 1000))
        self.trainingDataGS += str(millis)
        self.trainingDataInstr += str(millis)
        self.trainingDataGS += '.txt'
        self.trainingDataInstr += '.txt'

        # Put in file
        self.trainingDataGS = '/GameStates/' + str(self.trainingDataGS)
        self.trainingDataInstr = '/NetworkInstructions/' + str(self.trainingDataInstr)

        self.trainingDataGS = open(str(self.trainingDataGS), "w+")
        self.trainingDataInstr = open(str(self.trainingDataInstr), "w+")
        self.fileOpened = True

    def saveToTrainingdata(self, gameState, instr):
        # ensure at end of file
        self.trainingDataGS.write(gameState)
        self.trainingDataInstr.write(instr)

    def arraysToCSV(self, arr1, arr2):
        str1 = ""
        for i in range(len(arr1)):
            str1 += arr1[i]
            str1 += ","

        # remove trailing comma
        str1 = str1[:len(str1)-2]

        str2 = ""
        for i in range(len(arr2)):
            str2 += arr2[i]
            str2 += ","

        # remove trailing comma
        str2 = str2[:len(str2) - 2]

        # check if files are open
        if not self.fileOpened:
            self.openFiles()

        self.saveToTrainingdata(str1, str2)


    def closeFiles(self):
        self.trainingDataInstr.close()
        self.trainingDataGS.close()

    def preprocess(self, data):
        newData = np.zeros(73)
        # card Ids
        newData[:5] = data[:5]/8
        # time
        newData[5] = data[5]/180
        # elixir
        newData[6] = data[6]/10
        # princess tower health - enemy
        newData[7] = data[7] / 2543
        newData[9] = data[9] / 2543
        # king tower health - enemy
        newData[8] = data[8]/4008
        # unit Ids - enemy
        newData[10:38:3] = data[10:38:3]/8
        # unit xPosition - enemy
        newData[11:39:3] = data[11:39:3]/18
        # unit yPosition - enemy
        newData[12:40:3] = data[12:40:3]/32
        # princess tower health - own
        newData[40] = data[40] / 2543
        newData[42] = data[42] / 2543
        # king tower health - own
        newData[41] = data[41] / 4008
        # unit Ids - own
        newData[43:71:3] = data[43:71:3] / 8
        # unit xPosition - own
        newData[44:72:3] = data[44:72:3] / 18
        # unit yPosition - own
        newData[45::3] = data[45::3] / 32

        return newData

    def createInputData(self, newState):
        self.GameFrames[self.frameCnt][:73] = newState

        if self.frameCnt == 0:  # first gameState received
            self.GameFrames[self.frameCnt][73:139] = newState[7:]
            self.GameFrames[self.frameCnt][139:205] = newState[7:]
            self.GameFrames[self.frameCnt][205:271] = newState[7:]
        elif self.GameFrames[0][5] - self.GameFrames[self.frameCnt][5] < 14/180:    # first 14 seconds of gameStates (10 elixir)
            self.GameFrames[self.frameCnt][73:139] = self.GameFrames[self.frameCnt * 2/3][7:73]
            self.GameFrames[self.frameCnt][139:205] = self.GameFrames[self.frameCnt * 1/3][7:73]
            self.GameFrames[self.frameCnt][205:271] = self.GameFrames[0][7:73]
        else:
            crawler = self.frameCnt
            #find 14 selcond ago (10 elixir)
            while self.GameFrames[crawler][5] - self.GameFrames[self.frameCnt][5] < 14/180:
                crawler -= 1

            self.GameFrames[self.frameCnt][73:139] = self.GameFrames[self.frameCnt - (self.frameCnt - crawler) * 1 / 3][7:73]
            self.GameFrames[self.frameCnt][139:205] = self.GameFrames[self.frameCnt - (self.frameCnt - crawler) * 2 / 3][7:73]
            self.GameFrames[self.frameCnt][205:271] = self.GameFrames[self.frameCnt - (self.frameCnt - crawler)][7:73]

        # increment frameCnt
        self.frameCnt += 1

        return self.GameFrames[self.frameCnt-1]

    def createActionMap(self, outputs):
        cxy = np.argmax(outputs)
        self.ActionMaps[self.frameCnt-1][cxy[0]][cxy[1]][cxy[2]] = 1
        return self.ActionMaps[self.frameCnt-1]

    def reward(self, gameData, actionMaps):
        stateScore = np.zeros([gameData.shape[0]-1])
        for i in range(gameData.shape[0]-1):
            # add scores for tower damage
            stateScore[i] = (1 - self.GameFrames[i][7]) * 100
            stateScore[i] += (1 - self.GameFrames[i][9]) * 100
            stateScore[i] += (1 - self.GameFrames[i][8]) * 200

            # deduct scores for tower damage
            stateScore[i] -= (1 - self.GameFrames[i][40]) * 100 * 2
            stateScore[i] -= (1 - self.GameFrames[i][42]) * 100 * 2
            stateScore[i] -= (1 - self.GameFrames[i][41]) * 200 * 2

            # give bonus points for destroying towers
            stateScore[i] = stateScore[i] + 300 if (self.GameFrames[i][7 | 9] == 0) else stateScore[i]
            stateScore[i] = stateScore[i] + 500 if (self.GameFrames[i][7 & 9] == 0) else stateScore[i]
            stateScore[i] = stateScore[i] + 600 if (self.GameFrames[i][8] == 0) else stateScore[i]

            # deduct points for losing towers
            stateScore[i] = stateScore[i] - 300 * 2 if (self.GameFrames[i][40 | 42] == 0) else stateScore[i]
            stateScore[i] = stateScore[i] - 500 * 2 if (self.GameFrames[i][40 & 42] == 0) else stateScore[i]
            stateScore[i] = stateScore[i] - 600 * 2 if (self.GameFrames[i][41] == 0) else stateScore[i]

            # pass through tanh
            stateScore[i] = np.tanh(stateScore[i])

        rewardMap = np.zeros([gameData.shape[0]-1, 5, 9, 16])
        #assign scores to decisions
        for i in range(gameData.shape[0] - 1):
            if np.argmax(actionMaps[i])[0] == 0:
                rewardMap[i] = stateScore[i] * self.ActionMaps[i]
            else:
                # Scoring of playing
                cardPlayed = np.argmax(i)[0]
                id = self.GameFrames[i][cardPlayed-1]
                crawler = i
                while self.GameFrames[crawler][43 | 46 | 49 | 52 | 55 | 58 | 61 | 64 | 67 | 70] == id:
                    crawler += 1
                rewardMap[i] = stateScore[crawler] * actionMaps[i]

        self.rewards = rewardMap

        return rewardMap

    @staticmethod
    def transformReward(reward):
        return np.sign(reward)

    def fit_batch(self, model, gameData, gameActions, gamma=0.99):

        # Collect Data
        startStates = gameData
        nextStates = gameData[1:]
        nextStates[gameData.shape[0]-1] = gameData[-1]

        # Convert Actions to maps
        if len(gameActions.shape) > 4:
            actionMaps = np.zeros([gameData.shape[0], 5, 9, 16])
            for i in range(gameData.shape[0]-1):
                actionMaps[i][gameActions[i][0]][gameActions[i][1]][gameActions[i][2]] = 1
        else:
            actionMaps = gameActions

        # Predict Q Values
        nextQvals = model.predict([nextStates, np.ones(actionMaps[:gameData.shape[0]-1].shape)])

        # condition rewards
        reward = Network.transformReward(self.reward(gameData, actionMaps))

        # Calculate Q values
        Qvals = reward + gamma * np.max(nextQvals)

        # Fit Keras model
        model.fit(
            [startStates, self.ActionMaps], self.ActionMaps * Qvals[:, None],
            nb_epoch=1, batch_size=len(startStates), verbose=0
        )

        return model

    def model(self):
        # Create input layer
        inp = keras.layers.Input([self.GameFrames.shape[1]], name='input')
        # Create 2 100 neuron hidden layers
        hiddenL1 = keras.layers.Dense(100, activation='tanh')(inp)
        hiddenL2 = keras.layers.Dense(100, activation='tanh')(hiddenL1)
        # Add a dropout layer of 0.2
        dropout = keras.layers.Dropout(0.2)(hiddenL2)
        # Add another hidden layer of 50 neuron
        hiddenL3 = keras.layers.Dense(50, activation='tanh')(dropout)

        out = keras.layers.Dense(720, activation='sigmoid')(hiddenL3)

        self.Model = keras.models.Model(inputs=inp, outputs=out)
        optimizer = keras.optimizers.RMSprop(lr=0.00025, rho=0.95, epsilon=0.01)
        self.Model.compile(optimizer, loss='mse')

        return self.Model


    def saveNetwork(self):
        # Save model
        model_json = self.model.to_json()
        with open("model.json:", "w") as json_file:
            json_file.write(model_json)

        # Save weights
        self.model.save_weights("model.h5")
        print("model saved to disk")

    def loadNetwork(self):
        #Load model
        json_file = open('model.json', 'r')
        loaded_model_json = json_file.read()
        json_file.close()
        loaded_model = keras.models.model_from_json(loaded_model_json)
        #load weights
        loaded_model.load_weights("model.h5")
        print('loaded model from disk')

    def fetchAnotherGameSave(self):
        stateEntries = os.listdir('/GameStates/')
        instrEntries = os.listdir('/NetworkInstructions/')

        rng = int(np.random.random() % len(stateEntries))

        gameData = open('/GameStates/' + stateEntries[rng], "r")
        gameActions = open('/NetworkInstructions/' + instrEntries[rng], "r")

        return gameData, gameActions
