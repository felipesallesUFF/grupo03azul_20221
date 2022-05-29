/*
    Fábrica de Software para Educação
    Professor Lauro Kozovits, D.Sc.
    ProfessorKozovits@gmail.com
    Universidade Federal Fluminense, UFF
    Rio de Janeiro, Brasil
    Subprojeto: Alchemie Zwei

    Partes do software registradas no INPI como integrantes de alguns apps para smartphones
    Copyright @ 2016..2022

    Se você deseja usar partes do presente software em seu projeto, por favor mantenha esse cabeçalho e peça autorização de uso.
    If you wish to use parts of this software in your project, please keep this header and ask for authorization to use.

 */

package br.uff.ic.lek.utils;

public class RandomNumber {
    public static double random(double min, double max){  //  interval [min..max) open at end
        return Math.random()*(max-min)+min;
    }
    public static int random(int min, int max){ //  interval [min..max]
        return (int) (Math.random()*(max-min+1)+min);
    }

    /* use as a test

        for (int i=0;i<100;i++){
            if (i%10==0) System.out.println();
            System.out.print (RandomNumber.random(10, 20) + " ");

        }
        System.out.println();
        for (int i=0;i<100;i++){
            if (i%10==0) System.out.println();
            System.out.print (RandomNumber.random(19.999999, 20.0) + " ");

        }
        System.out.println();
     */
}
