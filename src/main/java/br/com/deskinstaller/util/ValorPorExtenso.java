package br.com.deskinstaller.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Converte valores monetarios para extenso em portugues do Brasil.
 * Usado no recibo, onde o valor por extenso e praxe.
 *
 * Ex.: 1250.50 -> "um mil, duzentos e cinquenta reais e cinquenta centavos"
 *
 * @author Julio Izidoro
 */
public final class ValorPorExtenso {

    private static final String[] UNIDADES = {
            "", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove",
            "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete",
            "dezoito", "dezenove"
    };

    private static final String[] DEZENAS = {
            "", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"
    };

    private static final String[] CENTENAS = {
            "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos",
            "seiscentos", "setecentos", "oitocentos", "novecentos"
    };

    private ValorPorExtenso() {
    }

    /**
     * @param valor valor monetario (pode ser nulo)
     * @return valor por extenso com "reais"/"centavos"; string vazia se valor nulo
     */
    public static String converter(BigDecimal valor) {
        if (valor == null) {
            return "";
        }
        BigDecimal arredondado = valor.setScale(2, RoundingMode.HALF_UP).abs();
        long reais = arredondado.longValue();
        int centavos = arredondado.subtract(new BigDecimal(reais)).movePointRight(2).intValue();

        if (reais == 0 && centavos == 0) {
            return "zero real";
        }

        StringBuilder sb = new StringBuilder();
        if (reais > 0) {
            sb.append(porExtenso(reais)).append(reais == 1 ? " real" : " reais");
        }
        if (centavos > 0) {
            if (sb.length() > 0) {
                sb.append(" e ");
            }
            sb.append(porExtenso(centavos)).append(centavos == 1 ? " centavo" : " centavos");
        }
        return sb.toString();
    }

    /** Numero inteiro por extenso, ate a casa dos bilhoes. */
    private static String porExtenso(long numero) {
        if (numero == 0) {
            return "zero";
        }
        if (numero >= 1_000_000_000L) {
            long bilhoes = numero / 1_000_000_000L;
            long resto = numero % 1_000_000_000L;
            return juntar(porExtenso(bilhoes) + (bilhoes == 1 ? " bilhão" : " bilhões"), resto);
        }
        if (numero >= 1_000_000L) {
            long milhoes = numero / 1_000_000L;
            long resto = numero % 1_000_000L;
            return juntar(porExtenso(milhoes) + (milhoes == 1 ? " milhão" : " milhões"), resto);
        }
        if (numero >= 1_000L) {
            long milhares = numero / 1_000L;
            long resto = numero % 1_000L;
            return juntar(porExtenso(milhares) + " mil", resto);
        }
        return ateNovecentosENoventaENove((int) numero);
    }

    /** "cento e vinte" x "um mil, duzentos" — a virgula entra quando o resto tem centena. */
    private static String juntar(String prefixo, long resto) {
        if (resto == 0) {
            return prefixo;
        }
        String separador = (resto < 100 || resto % 100 == 0) ? " e " : ", ";
        return prefixo + separador + porExtenso(resto);
    }

    private static String ateNovecentosENoventaENove(int numero) {
        if (numero == 100) {
            return "cem";
        }
        StringBuilder sb = new StringBuilder();
        int centena = numero / 100;
        int resto = numero % 100;

        if (centena > 0) {
            sb.append(CENTENAS[centena]);
        }
        if (resto > 0) {
            if (sb.length() > 0) {
                sb.append(" e ");
            }
            if (resto < 20) {
                sb.append(UNIDADES[resto]);
            } else {
                sb.append(DEZENAS[resto / 10]);
                if (resto % 10 > 0) {
                    sb.append(" e ").append(UNIDADES[resto % 10]);
                }
            }
        }
        return sb.toString();
    }
}
