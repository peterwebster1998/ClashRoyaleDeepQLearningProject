import socket
import json
import numpy as np

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

port = 7000
ip = '192.168.0.87'

try:
    s.connect(('localhost', port))
    print('Connection Successful')
    playAgain = True
    playing = False

    while playAgain:
        playAgain = False

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
            print('playing =', playing)

            if playing:
                ### Receive GameState
                data = s.recv(64*73)
                gameState = np.array(json.loads(data))
                # print('gameState:')
                # print(gameState[:5])
                # print(gameState[5:7])
                # for i in range(0, 22):
                #     print(gameState[3*i+7: i*3+10])
                # print(gameState[7:10])
                # print(gameState[40:43])


                ### Save Gamestate to training set

                ### Give State to Network

                ### Receive output from network
                instr = np.random.random(3)
                instr[0] = int(instr[0] * 4)
                instr[1] = instr[1] * 18
                instr[2] = instr[2] * 32

                ### Save output to training data

                ### Send instructions to game
                instr = json.dumps(instr.tolist())
                s.send((instr + '\n').encode())


        ### Receive final game results
        data = s.recv(6*64)
        finalStates = np.array(json.loads(data))

        ### Send playagain intsructions
        instr = 0
        instr = json.dumps(instr)
        s.send(instr)

    ### Compile training sets

    ### Train

    ### Save weights & biases to file


except Exception as e:
    print(e)
finally:
    s.close()
