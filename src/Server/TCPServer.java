package Server;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

public class TCPServer {

    public static void main (String args[]) throws IOException {
    	
    	SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        
    	ArrayList <Conta> contas = new ArrayList<>();
    	
        Conta c1 = new Conta();
        Conta c2 = new Conta();
        Conta c3 = new Conta();
        Conta c4 = new Conta();
        Conta c5 = new Conta();
        
        c1.setNome("Samuel Alcantara");
        c1.setAgencia("2055");
        c1.setConta("12345678-9");
        c1.setValorEmCaixa(5000.00);
        c1.setExtrato("");
        contas.add(c1);
        
        c2.setNome("Sebastiao Pereira");
        c2.setAgencia("2056");
        c2.setConta("87654321-0");
        c2.setValorEmCaixa(7000.00);
        c2.setExtrato("");
        contas.add(c2);
        
        c3.setNome("Eunice Santos");
        c3.setAgencia("2057");
        c3.setConta("23456789-0");
        c3.setValorEmCaixa(6000.00);
        c3.setExtrato("");
        contas.add(c3);
        
        c4.setNome("Elyakin Araujo");
        c4.setAgencia("2058");
        c4.setConta("21345678-5");
        c4.setValorEmCaixa(4590.50);
        c4.setExtrato("");
        contas.add(c4);
        
        try{
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket, contas);
            }
        } catch(IOException e) {
        	System.out.println("Listen socket:"+e.getMessage());
        }
    }
}

class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    TEA tea = new TEA();
    
    ArrayList <Conta> contas = new ArrayList<>();
    
    public Connection (Socket aClientSocket, ArrayList <Conta> contas) {
        try {
        	this.contas = contas;
            clientSocket = aClientSocket;
            in = new DataInputStream( clientSocket.getInputStream());
            out =new DataOutputStream( clientSocket.getOutputStream());
            this.start();
        } catch(IOException e) {
        	System.out.println("Connection:"+e.getMessage());
        }
    }
	
    SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
	
    public void run(){
    	
        try {
        	String rec = in.readUTF();
        	String recebe = tea.decrypt(rec);
        	System.out.println("MENSGEM RECEBIDA CRIPTOGRAFADA: " + rec);
            String data = recebe.trim();	// LER MENSAGEM DO CLIENTE
            
            String mensagem = ManipulaConta(data);
            
            String envia = tea.encrypt(mensagem);
            System.out.println("MENSAGEM ENVIADA CRIPTOGRAFADA: " + envia);
            out.writeUTF(envia); // RETORNA MENSAGEM DO SERVER
            
        } catch (EOFException e){
        	System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {
        	System.out.println("readline:"+e.getMessage());
        } finally{
        	try {
        		clientSocket.close();
        	}catch (IOException e){
        		/*close failed*/
        	}
        }


    }
    
    public String ManipulaConta(String data) {
    	
        String operacao = data.substring(0, 1);
        String mensagem = null;
        
    	if(operacao.equals("$")) { 			//SALDO - OK
    		mensagem = saldo(data);
    	}else if(operacao.equals("D")) { 	//DEPOSITO - OK
    		mensagem = deposito(data);
    	}else if(operacao.equals("*")) { 	//EXTRATO - 
    		mensagem = extrato(data);
    	}else if(operacao.equals("S")) { 	//SAQUE - OK
    		mensagem = saque(data);
    	}else if(operacao.equals("T")) { 	//TRANSFERENCIA - OK
    		mensagem = transferencia(data);
    	}
    	
        return mensagem;
        
    }
        
    public String saldo(String data) {
		
		String mensagem = "0";
    	int a = data.indexOf("|");

		for(int i = 0; i < contas.size(); i++) {
			if(data.substring(2, a).equals(contas.get(i).agencia) && data.substring(a+1).equals(contas.get(i).conta)) {
				mensagem = contas.get(i).agencia+"/"+contas.get(i).conta+"|"
							+contas.get(i).nome+"["+Double.toString(contas.get(i).valorEmCaixa);
				break;
			}
		}
    	return mensagem;
    }
    public String deposito(String data) {
		
    	String mensagem = "0";
    	int a = data.indexOf("|");
		int b = data.indexOf("[");
		
		for(int i = 0; i < contas.size(); i++) {
			if(data.substring(2,a).equals(contas.get(i).agencia) && data.substring(a+1, b).equals(contas.get(i).conta)) {
				mensagem = contas.get(i).agencia+"/"+contas.get(i).conta+"|"
						+contas.get(i).nome+"["+data.substring(b+1);
				contas.get(i).setValorEmCaixa(contas.get(i).valorEmCaixa+Double.parseDouble(data.substring(b+1)));
				String extra = contas.get(i).getExtrato();
				contas.get(i).setExtrato(extra+"|"+date.format(new Date())+" - DEPOSITO - "+data.substring(b+1));
				break;
			}
		}
    	return mensagem;
    }
    public String extrato(String data) {
    	
    	String mensagem = "0";
    	int a = data.indexOf("|");
    	
    	for(int i = 0; i < contas.size(); i++) {
    		if(data.substring(2, a).equals(contas.get(i).agencia) && data.substring(a+1).equals(contas.get(i).conta)) {
				mensagem = contas.get(i).getExtrato();
				break;
			}
    	}
    	
    	return mensagem;
    }
    public String saque(String data) {
    	
    	String mensagem = "0";
    	int a = data.indexOf("|");
		int b = data.indexOf("[");
    	
    	for(int i = 0; i < contas.size(); i++) {
    		if(data.substring(2, a).equals(contas.get(i).agencia) && data.substring(a+1, b).equals(contas.get(i).conta) && Double.parseDouble(data.substring(b+1)) <= contas.get(i).valorEmCaixa) {
    			
    			double saldo = contas.get(i).valorEmCaixa - Double.parseDouble(data.substring(b+1));
    			
    			contas.get(i).setValorEmCaixa(contas.get(i).valorEmCaixa-Double.parseDouble(data.substring(b+1)));
    			
    			mensagem = contas.get(i).agencia+"/"+contas.get(i).conta+"|"
						+contas.get(i).nome+"["+data.substring(b+1)+"]"+
    					Double.toString(saldo);
    			String extra = contas.get(i).getExtrato();
				contas.get(i).setExtrato(extra+"|"+date.format(new Date())+" - SAQUE - "+data.substring(b+1));
				
    			break;
    		}
    	}
    	
    	return mensagem;
    }
    public String transferencia(String data) {
    	
    	String mensagem = "0";
    	int a = data.indexOf("|");
		int b = data.indexOf("[");
		int c = data.indexOf("]");
		int d = data.indexOf("(");
		
    	for(int i = 0; i < contas.size(); i++) {
    		if(data.substring(2, a).equals(contas.get(i).agencia) && data.substring(a+1, b).equals(contas.get(i).conta) && Double.parseDouble(data.substring(d+1)) <= contas.get(i).valorEmCaixa) {
    			for(int j = 0; j < contas.size(); j++) {
    				if(data.substring(b+1, c).equals(contas.get(j).agencia) && data.substring(c+1, d).equals(contas.get(j).conta)) {
    					contas.get(i).setValorEmCaixa(contas.get(i).valorEmCaixa - Double.parseDouble(data.substring(d+1)));
    					contas.get(j).setValorEmCaixa(contas.get(j).valorEmCaixa + Double.parseDouble(data.substring(d+1)));
    	    			mensagem = contas.get(i).agencia+"/"+contas.get(i).conta+"|"
    							+contas.get(i).nome+"["+contas.get(j).agencia+"]"+contas.get(j).conta+"("+
    	    					contas.get(j).nome+")"+data.substring(d+1); 
    	    			String extra = contas.get(i).getExtrato();
    	    			String extr = contas.get(j).getExtrato();
    	    			contas.get(i).setExtrato(extra+"|"+date.format(new Date())+" - TRANSFERENCIA - "+data.substring(d+1));
    	    			contas.get(j).setExtrato(extr+"|"+date.format(new Date())+" - TRANSFERENCIA - "+data.substring(d+1));
    					break;
    					
    				}
    			}
    			break;
    		}
    	}
    	return mensagem;
    }
}