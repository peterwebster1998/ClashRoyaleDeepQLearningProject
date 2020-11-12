# ClashRoyaleDeepQLearningProject
A currently unfinished project of mine, in which I am working to teach a Deep Q Network to play the mobile game Clash Royale

The src file is src from the IntelliJ Java project in which I have created a primitive version of Clash Royale. It utilizes the stdlib library made by Princeton University & the JSON library for the encoding of the data which is sent to the network files which are written in python.

Currently in the Java code there is an issue with the troop path finding, which is in the proces of being rewritten.

The python files receive the JSON arrays through the handler file, which is then passed onto the network for interpretation. The network produces an action as a response and then it is sent back to the Java game using the JSON arrays again

The python model is not yet polished so also requires fixing.

The .csv file contains the stats for the 8 troop cards I have decided to incorperate, when this is working there is no reason that more cards could not be implemented.

I have uploaded these files to demonstrate my coding ability to potential employers, although this is currently unfinished I believe it is a testament to the capability and problem solving skills I posess
