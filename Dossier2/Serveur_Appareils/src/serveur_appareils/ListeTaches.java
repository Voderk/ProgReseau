/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_appareils;

import java.util.LinkedList;

/**
 *
 * @author Alex
 */
public class ListeTaches implements SourceTache
{

    private LinkedList listeTaches;
    
    public ListeTaches()
    {
        listeTaches = new LinkedList();
    }
    
    public synchronized Runnable getTache() throws InterruptedException {
        System.out.println("getTache avant wait");
        while(!existTaches())
            wait();
        return (Runnable)listeTaches.remove();
    }

    public synchronized boolean existTaches() {
        return !(listeTaches.isEmpty());
    }

    public synchronized void recordTache(Runnable r) {
        listeTaches.addLast(r);
        System.out.println("ListeTache dans la file");
        notify();
    }
    
}
