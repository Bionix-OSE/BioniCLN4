import socket
import threading

host = "127.0.0.1"
port = 1507
server = socket.socket()
server.bind((host, port))
server.listen()
print("Server listening on", host, ":", port)

clients = []

def broadcast(message, sender):
	for client in clients:
		if client != sender:
			try:
				client.send(message.encode())
			except:
				clients.remove(client)

def chandler(client, addr):
	while True:
		try:
			message = client.recv(1024).decode()
			if not message:
				break
			if message.endswith(": q"):
				break
			print(message)
			broadcast(message, client)
		except:
			break
	clients.remove(client)
	client.close()
	print(addr, "disconnected")

while True:
	print("Waiting for connections...")
	conn, addr = server.accept()
	addr = str(addr)
	print("Connection from:", addr)
	clients.append(conn)
	threading.Thread(target=chandler, args=(conn, addr)).start()
