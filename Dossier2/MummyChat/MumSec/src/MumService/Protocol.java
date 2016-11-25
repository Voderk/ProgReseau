/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MumService;

/**
 *
 * @author Gauvain Klug
 */
public class Protocol 
{
    public static String ExtractProtocol(String s)
    {
        String[] ssplit = s.split(" ");
        return ssplit[0];
    }
    
    public static String ExtractMessage(String s)
    {
        String subs = s.substring(s.indexOf(" ")+1);
        return subs;
    }        
    
    public static String ExtractTag(String s)
    {
        String subs = s.substring(s.indexOf("[")+2,s.indexOf("]") );
        return subs;
    }
    
    public static String ExtractParam(String s, int i)
    {
        String[] ssplit = s.split(" ");
        return ssplit[i];
    }
    
    public static String BuildMessage(String s , String proto)
    {
        if(protocols.ANSWER_QUESTION.toString().equalsIgnoreCase(proto))
            return proto + s ;
        if(protocols.LOGIN_GROUP.toString().equalsIgnoreCase(proto))
            return proto + s ;
        if(protocols.POST_EVENT.toString().equalsIgnoreCase(proto))
            return proto + s ;
        if(protocols.POST_QUESTION.toString().equalsIgnoreCase(proto))
            return proto + s ;
        return s;
    }
    
    public enum protocols{
        LOGIN_GROUP,
        POST_QUESTION,
        ANSWER_QUESTION,
        POST_EVENT
    }
}
