package br.com.cod3r.calculadora.Model;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
    private enum TipoComando{
        ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA, REST, POSN;    
    };



    private static final Memoria instancia = new Memoria();
    private TipoComando ultimaOperao = null;
    private boolean substituir = false;
    private String textoAtual = "";  
    private String textoBuffer = "";
    private final List<MemoriaObservador> observadores = new ArrayList<>();
        

    public Memoria() {
        
    }

    public static Memoria getInstancia() {
        return instancia;
    }

    public void adicionarObservador(MemoriaObservador observador){
        observadores.add(observador);
    }

    public String getTextoAtual() {
        return textoAtual.isEmpty() ? "0" : textoAtual;
    }

    public void processarComando(String texto){

        TipoComando tipoComando = detectarTipoComando(texto);
        if (tipoComando == null) {
            return;
        } else if (tipoComando == tipoComando.ZERAR) {
            textoAtual = "";
            textoBuffer = "";
            substituir = false;
            ultimaOperao = null;
        } else if (tipoComando == tipoComando.POSN && textoAtual.contains("-")) {
            textoAtual = textoAtual.substring(1);
        } else if (tipoComando == tipoComando.POSN && !textoAtual.contains("-")) {
            textoAtual = "-"+textoAtual;
        } else if (tipoComando == tipoComando.NUMERO || tipoComando == tipoComando.VIRGULA) {
            textoAtual = substituir ? texto : textoAtual + texto;
            substituir = false;
        } else {
            substituir = true;
            textoAtual = obterResultadoOperacao();

            textoBuffer = textoAtual;

            ultimaOperao = tipoComando;
        }
        observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
    }

    private String obterResultadoOperacao() {
        if (ultimaOperao == null || ultimaOperao == TipoComando.IGUAL) {
            return textoAtual;
        } 
        double numeroBuffer = Double.parseDouble(textoBuffer.replaceAll(",", ".")) ;
        double numeroAtual = Double.parseDouble(textoAtual.replaceAll(",", ".")) ;

        double resultado = 0;

        if(ultimaOperao == TipoComando.SOMA) {
            resultado = numeroBuffer + numeroAtual;
        } else if (ultimaOperao == TipoComando.SUB) {
            resultado = numeroBuffer - numeroAtual;
        } else if (ultimaOperao == TipoComando.MULT) {
            resultado = numeroBuffer * numeroAtual;
        } else if (ultimaOperao == TipoComando.DIV) {
            resultado = numeroBuffer / numeroAtual;
        } else if (ultimaOperao == TipoComando.REST) {
            resultado = numeroBuffer % numeroAtual;
        } 
        String resultadoString = Double.toString(resultado).replace(".", ","); 
        boolean inteiro = resultadoString.endsWith(",0");
        return inteiro ? resultadoString.replace(",0", "") : resultadoString;
    }

    private TipoComando detectarTipoComando(String texto) {
        if (textoAtual.isEmpty() && texto == "0") {
            return null;    
        }
        try {
            Integer.parseInt(texto);
            return TipoComando.NUMERO;
        } catch (NumberFormatException e) {
            // quando nao for numero
            if ("AC".equals(texto)) {
                return TipoComando.ZERAR;
            } else if("/".equals(texto)){
                return TipoComando.DIV;
            } else if("+".equals(texto)){
                return TipoComando.SOMA;
            } else if("-".equals(texto)){
                return TipoComando.SUB;
            } else if("*".equals(texto)){
                return TipoComando.MULT;
            } else if("±".equals(texto)){
                return TipoComando.POSN;
            } else if("%".equals(texto)){
                return TipoComando.REST;
            } else if("=".equals(texto)){
                return TipoComando.IGUAL;
            } else if(",".equals(texto) && !textoAtual.contains(",")){
                return TipoComando.VIRGULA;
            }
        }
        
        return null;
    }
        
}
