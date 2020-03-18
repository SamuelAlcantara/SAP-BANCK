package Client;

import java.util.Arrays;

public class TEA {
    
    public String encrypt(String infile) {

        int j;
        long delta = 0x9e3779b9, text[], sum =0, k[] = { 78945677, 87678687, 234234, 234234 };
        text = ConverteLong('e', infile);
        
        for (int n = 0; n < 32; n++) {
            sum += delta;
            for (int i = 0; i < text.length; i++) {
                j = i + 1;
                text[i] += ((text[j] << 4) + k[0]) ^ (text[j]+sum) ^ ((text[j] >> 5) + k[1]);
                text[j] += ((text[i] << 4) + k[2]) ^ (text[i]+sum) ^ ((text[i] >> 5) + k[3]);
                i++;
            }
        }
        return Arrays.toString(text);
    }

    public String decrypt(String infile) {

        int j;
        long delta = 0x9e3779b9, sum = delta << 5, k[] = { 78945677, 87678687, 234234, 234234 }, text[];
        text = ConverteLong('d', infile);
        
        for (int n = 0; n < 32; n++) {
            for (int i = 0; i < text.length; i++) {
                j = i + 1;
                text[j] -= ((text[i] << 4) + k[2]) ^ (text[i]+sum) ^ ((text[i] >> 5) + k[3]);
                text[i] -= ((text[j] << 4) + k[0]) ^ (text[j]+sum) ^ ((text[j] >> 5) + k[1]);
                i++;
            }
            sum -= delta;
        }
        return ConverteString(text);
    }
    
    public String ConverteString(long text[]){
        String mensagem = "";
        for (int i = 0; i < text.length; i++) {
            mensagem += (char)text[i];
        }
        return mensagem;
    }
    
    public long[] ConverteLong(char mode, String infile){
    	/* mode is 'e' for encrypt, 'd' for decrypt, infile is the message.*/
        long text[] = null;
        switch (mode) {
            case 'e':
                if (infile.length() % 2 == 0) { text = new long[infile.length()]; }
                else { text = new long[infile.length() + 1]; }
                for (int i = 0; i < infile.length(); i++) {
                    text[i] = infile.charAt(i);
                } break;
            case 'd':
                String msg[] = infile.substring(1, infile.length() - 1).split(",");
                if (msg.length % 2 == 0) { text = new long[msg.length]; }
                else { text = new long[msg.length + 1]; }
                for (int i = 0; i < msg.length; i++) {
                    if (!msg[i].contains("-")) { msg[i] = "+" + msg[i].trim(); }
                }
                for (int j = 0; j < msg.length; j++) {
                    text[j] = Long.valueOf(msg[j].trim());
                } break;
        }
        return text;
    }
}