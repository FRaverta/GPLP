package dtn;
import java_cup.runtime.*;
import java.io.Reader;
%% //inicio de opciones
      
/* 
    Nombre de la clase .java que crea
*/
%public %class DTNFormatLexer

/*
    Activar el contador de lineas, variable yyline
    Activar el contador de columna, variable yycolumn
*/
%line
%column
    
/* 
   Activamos la compatibilidad con Java CUP
*/
%cup
   
/*
    Declaraciones

    El codigo entre %{  y %} sera copiado integramente en el 
    analizador generado.
*/
%{    
    /*  Generamos un java_cup.Symbol para guardar el tipo de token 
        encontrado */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Generamos un Symbol para el tipo de token encontrado 
       junto con su valor */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
    
Espacio= [ \t\r\n ] 
Digit = [0-9]
Alpha = [a-zA-Z_]
ASCII = [^"\""] //Todos los caracteres del codigo ASCII que consideramos validos.
ComentarioLinea= "//".*\n 
%state COMENTARIO
%%

<YYINITIAL> {
    /* Regresa que el token la palabra reservada. */
	"NODES" {   return symbol(sym.NODES); }
    "Nodes" {   return symbol(sym.NODES); }
    "nodes" {   return symbol(sym.NODES); }
    "INTERVALS" {   return symbol(sym.INTERVALS); }
    "Intervals" {   return symbol(sym.INTERVALS); }
    "intervals" {   return symbol(sym.INTERVALS); }
    
    // Comentarios 
    {ComentarioLinea} { }

    //Ignoro espacios
    {Espacio} { }



    //Delimitadores
    "(" {  return  symbol(sym.LBRACKET);   }
    ")" {  return  symbol(sym.RBRACKET);   }
    
    //Llaves.
    "{" {  return  symbol(sym.LBRACE);   }
    "}" {  return  symbol(sym.RBRACE);   }

    //Literales
    {Digit}{Digit}* {  return symbol(sym.INT,new Integer(yytext()));   }
   	
   	"\""{ASCII}*"\"" {  return symbol(sym.STRING,yytext());  }
    "/*"        {yybegin(COMENTARIO);     }

      .   {   System.out.println ("Ilegal Char: " + yytext() + " at: line " + yyline + " ,column " + yycolumn);
      }    
}

    <COMENTARIO> {
         {Espacio}  {}
        "*/"       {yybegin(YYINITIAL); }
        .           {}

     }
                     
                         