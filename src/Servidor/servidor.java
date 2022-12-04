package Servidor;

import java.io.*;
import java.net.*;
import java.util.Random;

public class servidor {

    private static Integer port = 20312;

    private static String welcomeMsg = "--- Bem vindo a Rock, Paper, Scissors, Lizard, Spock Server na port " + port + " --- \n";

    public static int stringtonumber (String op) { //mudar entre numero e nome para as opções
        int ans = 0;
        if(op.equals("rock"))
            ans = 0;
        if(op.equals("paper"))
            ans = 1;
        if(op.equals("scissors"))
            ans = 2;
        if(op.equals("lizard"))
            ans = 3;
        if(op.equals("spock"))
            ans = 4;
        return ans;
    }

    public static String numbertostring (int op) { //mudar entre numero e nome para as opções
        String ans = "";
        if(op == 0)
            ans = "rock\n";
        if(op == 1)
            ans = "paper\n";
        if(op == 2)
            ans = "scissors\n";
        if(op == 3)
            ans = "lizard\n";
        if(op == 4)
            ans = "spock\n";
        return ans;
    }

    private static Integer[][] resJog = new Integer[5][5]; //é indexada por linha = servidor, coluna = cliente
    public static void abrir_resJog() { //matriz de valores de jogadas (1 vitoria do servidor, 0 empate, -1 vitoria cliente)
        resJog[0][0] = 0; resJog[0][1] = -1; resJog[0][2] = 1; resJog[0][3] = 1; resJog[0][4] = -1;
        resJog[1][0] = 1; resJog[1][1] = 0; resJog[1][2] = -1; resJog[1][3] = -1; resJog[1][4] = 1;
        resJog[2][0] = -1; resJog[2][1] = 1; resJog[2][2] = 0; resJog[2][3] = 1; resJog[2][4] = -1;
        resJog[3][0] = -1; resJog[3][1] = 1; resJog[3][2] = -1; resJog[3][3] = 0; resJog[3][4] = 1;
        resJog[4][0] = 1; resJog[4][1] = -1; resJog[4][2] = 1; resJog[4][3] = -1; resJog[4][4] = 0;
    }

    public static int randomInt(int min, int max) { //usado na heuristica do servidor
        Random random = new Random();
        return random.nextInt(max - min) + min; //max exclusive, min is inclusive
    }

    public static void main(String[] args) throws Exception {
        int resClient = -1;
        String inputClient = "";
        //int inputClient;
        //String resServidor = "";
        int resServidor = -1;
        abrir_resJog();

        System.out.println(servidor.welcomeMsg);

        ServerSocket sock = new ServerSocket(servidor.port);
        System.out.println("\nEstamos rodando na porta " + sock.getLocalPort() + " ..."); //imprime a porta, vemos se está batendo a info

        // Cliente
        Socket client = sock.accept();
        if (client.isConnected()) {
            System.out.println("\nCliente (" + (client.getLocalAddress().toString()).substring(1) + ":"
                    + client.getLocalPort() + ") entrou no jogo ...");
        }
        DataOutputStream outClient = new DataOutputStream(client.getOutputStream());
        BufferedReader inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

        int rodadas = 0; //para controlarmos em que rodada está
        int vitoria = 0; //Se vitoria>0, o servidor ganha, se vitoria<0 o cliente ganha, se vitoria = 0, empate
        int placarS = 0; //Anota as vitorias do servidor
        int placarC = 0; //Anota as vitorias do cliente

        while (!sock.isClosed()){
        //Heuristica Servidor
            if(placarS==placarC){ //se esta empatado o servidor fica distraído, jogando aleatoriamente
                resServidor = randomInt(0,5);
            }
            else if(placarS > placarC){ //se esta ganhando, imita a ultima jogada do cliente
                resServidor = stringtonumber(inputClient);
            }
            else if(placarS < placarC && rodadas < 8){ //se está perdendo na primeira metade do jogo, confia em pedra e papel e tesoura
                resServidor = rodadas%3;
            }
            else if(placarS < placarC && rodadas >= 8) {//se está perdendo no final, confia apenas em Lagarto e Spock
                if(rodadas%4 == 0 || rodadas%4 == 1){
                    resServidor = 3 + (rodadas%4);
                }else if(rodadas%4 == 2 || rodadas%4 == 3){
                    resServidor = 1 + (rodadas%4);
                }
            }

            System.out.println("rodada: " + rodadas);


            inputClient = inClient.readLine();
            int input = stringtonumber(inputClient);
            int resultado = resJog[resServidor][input];
            if (resultado == -1){
            System.out.println("Cliente jogou: " + inputClient);
            System.out.println("Servidor jogou: " + numbertostring(resServidor));
                resClient = resServidor;
                placarC++;
            System.out.println("Cliente ganhou a rodada!");
            }
            else if (resultado == 0){
            System.out.println("Cliente jogou: " + inputClient);
            System.out.println("Servidor jogou: " + numbertostring(resServidor));
                resClient = resServidor;
            System.out.println("Empate!");
            }
            else if (resultado == 1){
            System.out.println("Cliente jogou: " + inputClient);
            System.out.println("Servidor jogou: " + numbertostring(resServidor));
                resClient = resServidor;
                placarS++;
            System.out.println("Servidor ganhou a rodada!");
            }
            rodadas = rodadas+1;
            System.out.println("PLACAR: Servidor " + placarS + "x" + placarC + " Cliente\n");
            outClient.writeInt(resClient);

            if (rodadas == 15){
                if(placarS>placarC){
                    System.out.println("Servidor venceu o jogo");
                }else if(placarS==placarC){
                    System.out.println("Jogo terminou empatado!");
                }else if(placarS<placarC){
                    System.out.println("Cliente venceu o jogo");
                }
                client.close();
                sock.close();
            }
        }
    }

}
