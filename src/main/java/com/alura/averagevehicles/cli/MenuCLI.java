package com.alura.averagevehicles.cli;

import com.alura.averagevehicles.module.Dados;
import com.alura.averagevehicles.module.DadosAno;
import com.alura.averagevehicles.module.DadosModelos;
import com.alura.averagevehicles.module.Veiculo;
import com.alura.averagevehicles.service.ConsumoAPI;
import com.alura.averagevehicles.service.ConverterDados;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuCLI {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverterDados conversor = new ConverterDados();

    private final String baseURL = "https://parallelum.com.br/fipe/api/v1/";

    public void exibirMenu() {
        System.out.print("""
                ** Opções **
                
                1. Carros
                2. Motos
                3. Caminhoes
                """);

        System.out.print("\nEscolha uma das opções para consultar: ");
        int opcaoTipoVeiculo = leitura.nextInt();

        String endpoint = "";

        switch (opcaoTipoVeiculo) {
            case 1 -> { endpoint = baseURL.concat("carros/marcas"); }
            case 2 -> { endpoint = baseURL.concat("motos/marcas"); }
            case 3 ->  { endpoint = baseURL.concat("caminhoes/marcas"); }
        }

        var json = consumo.obterDados(endpoint);
        var marcas = conversor.obterLista(json, Dados.class);

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(marca -> {
                    System.out.println(marca.codigo()+ " | " + marca.nome());
                });

        System.out.print("\nDigite o codigo da marca para consulta: ");
        int opcaoMarcaVeiculo = leitura.nextInt();

        endpoint = endpoint + "/" + opcaoMarcaVeiculo + "/modelos";
        json = consumo.obterDados(endpoint);

        var modeloLista = conversor.obterDados(json, DadosModelos.class);

        System.out.println("\nModelos dessa marca: ");

        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(modelo -> {
                    System.out.println(modelo.codigo()+" | "+modelo.nome());
                });

        System.out.print("\nDigite o nome do carro a ser buscado: ");
        leitura.nextLine();
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(modelo -> modelo.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.stream()
                .forEach(modelo -> {
                    System.out.println(modelo.codigo()+" | "+modelo.nome());
                });

        System.out.print("\nDigite o código do modelo para obter os valores de avaliação: ");
        var opcaoModeloVeiculo = leitura.nextInt();

        endpoint = endpoint + "/" + opcaoModeloVeiculo + "/anos";
        json = consumo.obterDados(endpoint);

        List<DadosAno> anos = conversor.obterLista(json, DadosAno.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for(int i=0; i<anos.size(); i++) {
            var endpointAnos = endpoint + "/" + anos.get(i).codigo();
            json = consumo.obterDados(endpointAnos);

            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);

            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliação por ano: ");
        System.out.println("---------------------------");

        veiculos.forEach(veiculo -> {
            System.out.println("Valor: "+ veiculo.valor());
            System.out.println("Marca: "+ veiculo.marca());
            System.out.println("Modelo: "+ veiculo.modelo());
            System.out.println("Ano de modelo: "+ veiculo.anoModelo());
            System.out.println("Combustivel: "+ veiculo.combustive());
            System.out.println("---------------------------");
        });
    }
}
