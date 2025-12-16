package br.com.cod3r.calculadora.Model;

@FunctionalInterface
public interface MemoriaObservador {
    public void valorAlterado(String novoValor);
}
