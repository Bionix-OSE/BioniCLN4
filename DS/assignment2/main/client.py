import socket
import threading
from tkinter import *
import tkinter as tk

class ChatGUI(tk.Tk):
	def __init__(self):
		super().__init__()
		self.title("Chat Client")
		self.geometry("800x600")
		self.socket = None
		self.connected = False

		# Top half: Nickname, Server address, Port input, and Connect button
		top_frame = tk.Frame(self)
		top_frame.pack(pady=10)

		self.connect_button = tk.Button(top_frame, text="Connect", command=self.connect)
		self.connect_button.pack(side='left', padx=(10, 0))
		self.disconnect_button = tk.Button(top_frame, text="Disconnect", command=self.disconnect)
		self.disconnect_button.pack(side='left', padx=(10, 0))
		self.nick_label = tk.Label(top_frame, text="Nickname:")
		self.nick_label.pack(side='left')
		self.nick_entry = tk.Entry(top_frame)
		self.nick_entry.pack(side='left', expand=True, padx=(5, 0))
		self.addr_label = tk.Label(top_frame, text="Server Address:")
		self.addr_label.pack(side='left', padx=(10, 0))
		self.addr_entry = tk.Entry(top_frame)
		self.addr_entry.pack(side='left', expand=True, padx=(5, 0))
		self.port_label = tk.Label(top_frame, text="Port:")
		self.port_label.pack(side='left', padx=(10, 0))
		self.port_entry = tk.Entry(top_frame, width=5)
		self.port_entry.pack(side='left', padx=(5, 0))
		

		# Center frame: Chat display
		self.chat_display = tk.Text(self, state='disabled')
		self.chat_display.pack(fill='both', expand=True, pady=10)

		# Bottom half: Message input and send button
		bottom_frame = tk.Frame(self)
		bottom_frame.pack(fill='x', pady=10)

		self.message_entry = tk.Entry(bottom_frame)
		self.message_entry.pack(side='left', fill='x', expand=True, padx=(0, 10))
		self.message_entry.bind('<Return>', lambda event: self.send())

		self.send_button = tk.Button(bottom_frame, text="Send", command=self.send)
		self.send_button.pack(side='left')

	def send(self):
		message = self.message_entry.get()
		nickname = self.nick_entry.get()
		if not message:
			return

		if self.connected and self.socket:
			try:
				payload = f"{nickname}: {message}"
				self.socket.send(payload.encode())
			except Exception as e:
				self.chat_display.config(state='normal')
				self.chat_display.insert(tk.END, f"Send error: {e}\n")
				self.chat_display.config(state='disabled')
				return

		self.chat_display.config(state='normal')
		self.chat_display.insert(tk.END, f"{nickname}: {message}\n")
		self.chat_display.config(state='disabled')
		self.message_entry.delete(0, tk.END)

	def receive(self):
		while self.connected and self.socket:
			try:
				data = self.socket.recv(1024)
				if not data:
					break
				message = data.decode()
				self.chat_display.config(state='normal')
				self.chat_display.insert(tk.END, f"{message}\n")
				self.chat_display.config(state='disabled')
			except OSError:
				break
			except Exception as e:
				self.chat_display.config(state='normal')
				self.chat_display.insert(tk.END, f"Receive error: {e}\n")
				self.chat_display.config(state='disabled')
				break

	def connect(self):
		nickname = self.nick_entry.get()
		host = self.addr_entry.get()
		port = self.port_entry.get()
		if not nickname or not host or not port:
			return
		try:
			port = int(port)
			self.socket = socket.socket()
			self.socket.connect((host, port))
			self.connected = True
			threading.Thread(target=self.receive, daemon=True).start()
			self.chat_display.config(state='normal')
			self.chat_display.insert(tk.END, f"Connected to {host}:{port}.\n")
			self.chat_display.config(state='disabled')
		except Exception as e:
			self.chat_display.config(state='normal')
			self.chat_display.insert(tk.END, f"Connection error: {e}\n")
			self.chat_display.config(state='disabled')

	def disconnect(self):
		if self.connected and self.socket:
			try:
				nickname = self.nick_entry.get()
				self.socket.send(f"{nickname}: q".encode())
			except Exception as e:
				pass # Ignore send errors during disconnect
			try:
				self.socket.close()
			except Exception as e:
				pass # Ignore close errors
			self.connected = False
			self.chat_display.config(state='normal')
			self.chat_display.insert(tk.END, "Disconnected from server.\n")
			self.chat_display.config(state='disabled')

if __name__ == "__main__":
	app = ChatGUI()
	app.mainloop()
