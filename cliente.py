import socket
import random
import numpy as np

HOST = '172.15.1.146' # Identifica o nome do client
PORT = 20312 # Identifica a porta do client para comunicar com o servidor

# Lista com as possíveis jogadas que podem ser escolhidas pelo cliente.
opcoesJogadas = ['rock\n', 'paper\n', 'scissors\n', 'lizard\n', 'spock\n']
#opcoesJogadasNum = ['0\n', '1\n', '2\n', '3\n', '4\n']
def stringtonumber (op):
        ans = 0
        if(op == "rock\n"):
            ans = 0
        if(op == "paper\n"):
            ans = 1
        if(op == "scissors\n"):
            ans = 2
        if(op == "lizard\n"):
            ans = 3
        if(op == "spock\n"):
            ans = 4
        return ans
    

def numbertostring (op): 
    ans = ""
    if(op == 0):
        ans = "rock\n"
    if(op == 1):
        ans = "paper\n"
    if(op == 2):
        ans = "scissors\n"
    if(op == 3):
        ans = "lizard\n"
    if(op == 4):
        ans = "spock\n"
    return ans    

resJog = np.array([[0,-1,1,1,-1], 
                   [1,0,-1,-1,1], 
                   [-1,1,0,1,-1], 
                   [-1,1,-1,0,1], 
                   [1,-1,1,-1,0]], dtype = np.int32)
    
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

sock.connect((HOST, PORT)) # Parênteses duplo pq o connect tem apenas um parâmetro.
palpservidor = ""
placarC = 0
placarS = 0
rodadas = 0
while (rodadas < 15): # O cliente pode realizar n comunicações com o servidor, ou até essa ligação ser encerrada.
    
    print("Rodada: ", rodadas)
    if(placarS==placarC):
        mensagemEnvioClient = random.choice(opcoesJogadas) # Randomização da jogada de acordo com a lista opcoesJogadas.
    elif(placarS<placarC):
        mensagemEnvioClient = opcoesJogadas[rodadas%4] #nunca joga Spock
    elif(placarS>placarC and rodadas < 8):
        mensagemEnvioClient = palpservidor #imita o adversario
    elif(placarS>placarC and rodadas >= 8):
        mensagemEnvioClient = "spock\n" #so confia em spock para tentar virar
    print("\nCliente jogou:", mensagemEnvioClient)

    sock.sendall(str.encode(mensagemEnvioClient)) # Enviar mensagem para o servidor
    
    data =  sock.recv(1024)
    server = int.from_bytes(data, "big") #decodifica a msg

    print("Servidor jogou: ", numbertostring(server))
    palpservidor = numbertostring(server)
    intcli = stringtonumber(mensagemEnvioClient)
    if(resJog[server][intcli] == -1) : 
        placarC = placarC + 1
        print("Cliente ganhou a rodada!")
    elif(resJog[server][intcli] == 0) : 
        print("Empate!")
    elif(resJog[server][intcli] == 1) : 
        placarS = placarS + 1
        print("Servidor ganhou a rodada!")

    rodadas = rodadas + 1
    print("PLACAR: Servidor ", placarS, "x", placarC, " Cliente\n")
if(placarS>placarC) :
    print("Servidor venceu o jogo")
elif(placarS==placarC) :
    print("Jogo terminou empatado")
elif(placarS<placarC):
    print("Cliente venceu o jogo")