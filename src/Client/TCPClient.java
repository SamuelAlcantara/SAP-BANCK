package Client;

import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Date;
import java.util.Scanner;

import javax.xml.crypto.Data;

public class TCPClient {
	
	static String op;

    public static void main (String args[]) throws IOException {
        // arguments supply message and hostname
    	
    	do {
    		String mensagem = getDados();
            Socket s = null;
            
            try{
                int serverPort = 7896;
                s = new Socket("127.0.0.1", serverPort);
                DataInputStream in = new DataInputStream( s.getInputStream());
                DataOutputStream out =new DataOutputStream( s.getOutputStream());
                TEA tea = new TEA();
                String envia = tea.encrypt(mensagem);
                System.out.println("MENSAGEM ENVIADA CRIPTOGRAFADA: " + envia);
                out.writeUTF(envia);      	// UTF is a string encoding see Sn. 4.4
                String rec = in.readUTF();
                String recebe = tea.decrypt(rec);
                System.out.println("MENSGEM RECEBIDA CRIPTOGRAFADA: " + rec);
                String data = recebe.trim();	    // read a line of data from the stream
                //System.out.println("Received: "+ data + "\n\n") ;
                if(!data.equals("0"))
                	extrato(data);
                else
                	System.out.println("OPERAC√O N√O REALIZADA");
            } catch (UnknownHostException e){
            	System.out.println("Socket:"+e.getMessage());
            } catch (EOFException e){
            	System.out.println("EOF:"+e.getMessage());
            } catch (IOException e){
            	System.out.println("readline:"+e.getMessage());
            } finally { 
            	if(s!=null) try {
            		s.close();
            	}
            	catch (IOException e){
            		System.out.println("close:"+e.getMessage());
            	}
            }
    	}while(true);
    	
    }
    
    public static String getDados() {
    	
    	Scanner input = new Scanner(System.in);
    	boolean flag = true;
    	
    	do {
        	System.out.println("PARA REALIZAR UMA OPERACAO DIGITE UM DOS SEGUINTES VALORES:");
        	System.out.println("$ - SALDO | D - DEPOSITO | * - EXTRATO | S - SAQUE | T - TRANSFERENCIA");
        	
        	op = input.nextLine();
        	if(op.equals("$") || op.equals("D") || op.equals("*") || op.equals("S") || op.equals("T")) {
        		flag = false;
        	}else {
        		System.out.println("OPERACAO NAO REALIZADA TENTE NOVAMENTE");
        	}
        	
    	}while(flag);
    	

    	String mensagem = op;
    	String dados;
    	
    	if(op.equals("$")) { // "$" SALDO
			System.out.println("DIGITE A AGENCIA:");
			dados = input.nextLine();
			mensagem += "/" + dados;
			
			System.out.println("DIGITE A CONTA:");
			dados = input.nextLine();
			mensagem += "|" + dados;
			
		}
		if(op.equals("D")) { // "D" DEPOSITO
			System.out.println("DIGITE A AGENCIA:");
			dados = input.nextLine();
			mensagem += "/" + dados;
			
			System.out.println("DIGITE A CONTA: ");
			dados = input.nextLine();
			mensagem += "|" + dados;
			
			System.out.println("DIGITE O VALOR DO DEPOSITO:");
			dados = input.nextLine();
			mensagem += "[" + dados;
			
		}
		
		if(op.equals("*")) { // "*" EXTRATO
			System.out.println("DIGITE A AGENCIA:");
			dados = input.nextLine();
			mensagem += "/" + dados;
			
			System.out.println("DIGITE A CONTA:");
			dados = input.nextLine();
			mensagem += "|" + dados;
			
		}
		
		if(op.equals("S")) { // "S" SAQUE
			System.out.println("DIGITE A AGENCIA:");
			dados = input.nextLine();
			mensagem += "/" + dados;
			
			System.out.println("DIGITE A CONTA:");
			dados = input.nextLine();
			mensagem += "|" + dados;
			
			System.out.println("DIGITE O VALOR A SER SACADO:");
			dados = input.nextLine();
			mensagem += "[" + dados;
			
		}
		
		if(op.equals("T")) { // "T" TRANSFERENCIA
			System.out.println("DIGITE A AGENCIA DE ORIGEM:");
			dados = input.nextLine();
			mensagem += "/" + dados;
			
			System.out.println("DIGITE A CONTA DE ORIGEM:");
			dados = input.nextLine();
			mensagem += "|" + dados;
			
			System.out.println("DIGITE A AGENCIA DESTINO:");
			dados = input.nextLine();
			mensagem += "[" + dados;
			
			System.out.println("DIGITE A CONTA DESTINO:");
			dados = input.nextLine();
			mensagem += "]" + dados;
			
			System.out.println("DIGITE O VALOR DA TRANSFERENCIA:");
			dados = input.nextLine();
			mensagem += "(" + dados;
			
		}
    	
    	return mensagem;
    	
    }
    public static void extrato(String dados) {
    	/*
    	 *POLITICA DE SEPARA√á√ÉO DE STRING
    	 *SEPARADORES E ORDEM DE SEPARACAO
    	 * 1-/ 2-| 3-[ 4-] 5-( 6-)
    	 */
    	
    	SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
    	SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
    	
    	System.out.println("----------------SAP BANK----------------");
    	System.out.println("    AUTO ATENDIMENTO - AG:UNASP-SP");
    	System.out.println("DATA: " + date.format(new Date()) + "\t HORA: " + hour.format(new Date()));
    	
    	int x = dados.indexOf("/");
    	int y = dados.indexOf("|");
    	int z = dados.indexOf("[");
    	int w, m,n;
    	//    	SEQUENCIA SEPARADORES DE ARQUIVO/|[]();
    	if(op.equals("$")) { //SALDO
    		System.out.println("\n\tCOMPROVANTE DE SALDO\n");
    		System.out.println("Agencia: " + dados.substring(0, x));
    		System.out.println("Conta: " + dados.substring(x+1,y));
    		System.out.println("Nome: " + dados.substring(y+1, z));
    		System.out.printf("Saldo: %.2f\n\n\n", Double.parseDouble(dados.substring(z+1)));
    	}
    	if(op.equals("D")) { //DEPOSITO
    		System.out.println("\n\tCOMPROVANTE DE DEPOSITO\n");
    		System.out.println("Agencia: " + dados.substring(0, x));
    		System.out.println("Conta: " + dados.substring(x+1,y));
    		System.out.println("Nome: " + dados.substring(y+1, z));
    		System.out.printf("Valor Depositado: %.2f\n\n\n", Double.parseDouble(dados.substring(z+1)));
    	}
    	if(op.equals("*")) { //EXTRATO
    		System.out.println("\n\t\tEXTRATO\n");
    		String[] extra = dados.split("|");
    		for(int i = 0; i < extra.length; i++) {
    			if(!extra[i].equals("|")) {
    				System.out.print(extra[i]);
    			}else {
    				System.out.print("\n\n");
    			}
    		}
    		System.out.println("\n\n\n");
    	}
    	if(op.equals("S")) { //SAQUE
    		w = dados.indexOf("]");
    		System.out.println("\n\tCOMPROVANTE DE SAQUE\n");
    		System.out.println("Agencia: " + dados.substring(0, x));
    		System.out.println("Conta: " + dados.substring(x+1,y));
    		System.out.println("Nome: " + dados.substring(y+1, z));
    		System.out.printf("Valor Sacado: %.2f\n", Double.parseDouble(dados.substring(z+1, w)));
    		System.out.printf("Saldo: %.2f\n\n\n", Double.parseDouble(dados.substring(w+1)));
    	}
    	if(op.equals("T")) { //TRANSFERENCIA
    		
    		w = dados.indexOf("]");
    		m = dados.indexOf("(");
    		n = dados.indexOf(")");
    		System.out.println("\n\tCOMPROVANTE DE TRANSFERENCIA\n");
    		System.out.println("Agencia Origem: " + dados.substring(0, x));
    		System.out.println("Conta Origem: " + dados.substring(x+1,y));
    		System.out.println("Nome: " + dados.substring(y+1, z));
    		System.out.println("Agencia Destino: " + dados.substring(z+1, w));
    		System.out.println("Conta Destino: " + dados.substring(w+1, m));
    		System.out.println("Nome: " + dados.substring(m+1, n));
    		System.out.printf("Valor Transferido: %.2f\n\n\n", Double.parseDouble(dados.substring(n+1)));
    		
    	}
    }
}
