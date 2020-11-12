import socket
import json
import numpy as np

from Network import Network

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

port = 7000
ip = '192.168.0.87'

try:
    s.connect((ip, port))
    print('Connection Successful')
    playAgain = 0
    playing = False
    playerNo = json.loads(s.recv(4))

    ai = Network('trainingDataGS', 'trainingDataInstr')
    ai.model()

    while playAgain != 100:
        playAgain += 1
        # Start new game
        ai.newGame()

        # Receive Game start Signal - 4 bytes for an int
        one = s.recv(4)
        one = json.loads(one)

        if one is 1:
            print('1 recieved - Entering Game')
            playing = True
        else:
            print(one)
            print('Unknown input - Ending Training')

        while playing:
            ### check still playing
            playStatus = s.recv(4)
            playStatus = json.loads(playStatus)
            playing = (playStatus == 1)

            if playing:
                ### Receive GameState
                data = s.recv(64*73)
                gameState = np.array(json.loads(data))

                ### Preprocess the data
                processedData = ai.preprocess(gameState)
                inputData = ai.createInputData(processedData)

                ### Give State to Network
                outputData = ai.Model.predict_on_batch(inputData)

                ### Interpret output from network
                instr = np.argmax(outputData)
                instr[1:] *= 2

                ### Save output to training data
                ai.arraysToCSV(gameState, instr)

                ### Send instructions to game
                instr = json.dumps(instr)
                s.send((instr + '\n').encode())


        ### Receive final game results
        data = s.recv(6*64)
        finalStates = np.array(json.loads(data))

        ### Send playagain intsructions
        instr = 0 if playAgain is 100 else 1
        instr = json.dumps(instr)
        s.send(instr)


    if playerNo is 1:
        ### Train
        ai.fit_batch(ai.model, ai.GameFrames, ai.ActionMaps, gamma=0.99)
        # Train on 9 more gameFiles
        for i in range(9):
            gameData, gameActions = ai.fetchAnotherGameSave()
            ai.fit_batch(ai.model, gameData, gameActions, gamma=0.99)

        ### Save weights & biases to file
        ai.saveNetwork()

    ### Close open files
    ai.closeFiles()


except Exception as e:
    print(e.with_traceback())
finally:
    s.close()
