package com.mycompany.parserxmljson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class Principal {

    public static void main(String[] args) throws Exception {

        String inputXml = ExemploXML.inputXml;
        
        //// Outro exemplo de Xml.
        //String inputXml = "<bilhete>\n" +
        //"<para>José</para>\n" +
        //"<de>Maria</de>\n" +
        //"<corpo>Não me esqueça neste fim-se-semana</corpo>\n" +
        //"</bilhete>";
        int posicaoInicioChave = 0;

        try {
            //enquanto houver uma nova chave, faz as substituições
            while (posicaoInicioChave > -1) {
                posicaoInicioChave = inputXml.indexOf("<");

                if (posicaoInicioChave > -1) {
                    int posicaoFinalChave = inputXml.indexOf(">", posicaoInicioChave);
                    String chave = inputXml.substring(posicaoInicioChave + 1, posicaoFinalChave);

                    inputXml = marcaInicioEFinalValorDaChave(inputXml, chave);
                } else {
                    break;      //se não houver chave, sai do laço
                }
            }

            // regex para pegar valores entre | e ||
            Matcher m = Pattern.compile("[|][A-Za-z0-9. ][^:]*[||]").matcher(inputXml);
            while (m.find()) {
                var valor = m.group().replace("||\n||", "||}");
                inputXml = ajustaValoresTextoOuNumericos(valor, inputXml, m);
            }

            inputXml = substituicoesAdicionais(inputXml);

            m.reset();
            inputXml = marcaListas(inputXml, m);
            
            System.out.println(inputXml);
            
        } catch (Exception ex) {
            System.out.println("Não foi possível realizar a conversão.\nVerifique se o arquivo xml está correto.");
        }

    }

    /// Marca início do valor com | e final com ||.
    private static String marcaInicioEFinalValorDaChave(String inputXml, String chave) {
        inputXml = inputXml.replaceAll("<" + chave + ">", "\"" + chave + "\":|");     //substitui por ex. <tag> por chave:|
        inputXml = inputXml.replaceAll("</" + chave + ">", "||");                     //substitui por ex. </tag> por ||
        return inputXml;
    }

    private static String ajustaValoresTextoOuNumericos(String valor, String inputXml, Matcher m) {

        // regex para pegar números inteiros e decimais
        Matcher mValor = Pattern.compile("^[1-9]\\d*(\\.\\d+)?$").matcher(valor.substring(1, valor.indexOf("||")));

        if (mValor.find()) {
            valor = valor.replace("||}", "}");
            valor = valor.replace("||", ",");     //é número
            valor = valor.replace("|", "");
        } else {
            valor = valor.replace("||}", "\"}");
            valor = valor.replace("||", "\",");   //é texto
            valor = valor.replace("|", "\"");
        }

        inputXml = inputXml.replace(m.group(), valor);
        return inputXml;
    }

    private static String substituicoesAdicionais(String inputXml) {
        inputXml = inputXml.replace(",\n\t,", "\n\t},");
        inputXml = inputXml.replace(",\n\t}", "\n}");
        inputXml = inputXml.replace(":|", ":{");
        inputXml = inputXml.replace(",\n||", "}");
        return inputXml;
    }

    /// Marca as listas com colchetes.
    private static String marcaListas(String inputXml, Matcher m) {
        // Captura as chaves.
        m = Pattern.compile("[A-Za-z]+[\"][:][{]").matcher(inputXml);
        Set<String> chaves = new HashSet<String>();
        while (m.find()) {
            chaves.add(m.group());
        }
        Map<String, Integer> dicionario = new HashMap<String, Integer>();
        Iterator<String> chavesAsIterator = chaves.iterator();
        // Dicionario que guarda o número de ocorrências de cada chave.
        while (chavesAsIterator.hasNext()) {
            String chave = chavesAsIterator.next();
            dicionario.put(chave.substring(0, chave.indexOf(":")), StringUtils.countMatches(inputXml, chave));
        }
        Set<String> dicionarioChaves = dicionario.keySet();
        Iterator<String> iterator = dicionarioChaves.iterator();

        // Cada chave com mais de uma ocorrência é uma lista e recebe colchetes.
        while (iterator.hasNext()) {
            String chave = iterator.next();
            Integer numOcorrencias = dicionario.get(chave);
            if (numOcorrencias > 1) {

                int ultimaOcorrencia = inputXml.lastIndexOf(chave);
                int posUltimoLista = inputXml.indexOf("}", ultimaOcorrencia + chave.length());
                inputXml = inputXml.substring(0, posUltimoLista) + "}\n]\n" + inputXml.substring(posUltimoLista + 1);

                inputXml = inputXml.replaceFirst(chave + "+[:][{]", chave + ": [\n\t{");
                inputXml = inputXml.replaceAll("\"" + chave + "+[:][{]", "{");
            }
        }
        return inputXml;
    }
}
