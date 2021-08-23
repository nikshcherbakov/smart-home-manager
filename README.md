# Smart Home Manager

***Smart Home Manager*** is a project that can help you to receive the information about humidity and temperature in the room within your home wifi network. It is also possible to use Google Voice Api to a partucilar task with your smart home (for now it is only possible to turn a led on, but any other home device could be fit in instead).


<p align="center">
  <img width="782" alt="smart-home-manager-scheme" src="https://user-images.githubusercontent.com/74429654/130457411-079f53e2-fa7e-435c-a05e-0e51e3055b4d.png">
</p>


The application is devided into two parts:
1. The software part (whitten in Java)
2. The hardware part (Arduino + NodeMCU)

## Software part
The software is written in Android Studio. I implemented simple user interface with active button. When a user pushes the button, a Google Voice command window appears on the screen and the device is listening to a user's command. 


<p align="center">
  <img width="176" alt="google-voice" src="https://user-images.githubusercontent.com/74429654/130460723-1c4a02c8-f25b-47cd-a13e-912d6a9fa6a9.png">
</p>


If the command is equaled to the one in the memory, the correspondong action gets done and the button turns green (with "Complete" label on it). If there's not such command, then the button's collor changes to red and "Error" labels appears on it.

<p align="center">
  <img width="190" alt="interface" src="https://user-images.githubusercontent.com/74429654/130459165-9990ff33-112a-4f40-bac5-99bf62b39b4b.png">
  <img width="190" alt="interface2" src="https://user-images.githubusercontent.com/74429654/130459322-a30703d0-e7c9-48b2-b51b-f8aed613dda3.png">
<img width="190" alt="interface3" src="https://user-images.githubusercontent.com/74429654/130459393-94a9bc5c-af3e-43e6-b894-024306dbb3bf.png">
</p>

## Hardware part

The hardware part is implemented on the basis of the following shields and sensors:
1. Arduino Uno - collects data from temperature and humidity sensor
2. NodeMCU - receives packed data from Arduino and sends it to the "server" on a smartphone
3. DHT22 - a sensor to measure temperature and humidity in the room
