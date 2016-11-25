/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MumService;

import java.util.stream.IntStream;

/**
 *
 * @author Gauvain Klug
 */
public class Crypto {
    public static int h(String x)
    {
        IntStream charstream = x.chars();
        int[] chars = charstream.toArray();
        int sum = 0;
        for(int i = 0; i < chars.length; i++)
            sum += chars[i];
        return sum % 67;
    }
}
