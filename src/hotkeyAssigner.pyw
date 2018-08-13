import socket
import keyboard

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect(('localhost', 6969))

keyboard.wait("§")

sock.send("§ pressed".encode())