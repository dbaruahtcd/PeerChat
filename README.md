# PeerChat
Introduction

Every node is both client and server.
Any node can join the network knowing the IP address of any othet active node in the network.
Nodes listen for TCP communications on port 8767.
All messages are sent in JSON format.
A seperate TCP connection is created for sending messages and closed.
See PeerUI for various functionalities a node can do. It serves as an UI for the node.
Conditions

Prefix routing functionality not fully implemented.
Expects only public IP addresses.
Required a JSON jar.
