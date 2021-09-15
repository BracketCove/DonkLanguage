Donk Language represented with Backus-Naur Form:

This is written in BNF (EBNF)

Program -> Declaration* EOF ;

Declaration -> FunctionDeclaration |
               VariableDeclaration |
               ValueDeclaration |
               Statement ; 
               
ValueDeclaration -> "val" IDENTIFIER ":" 
                    "String" |
                    "Double" |
                    "Boolean" 
                    "="
                    STRING |
                    NUMBER 
                    ";"
                    
VariableDeclaration -> "var" IDENTIFIER ":" 
                    "String" |
                    "Double" |
                    "Boolean" 
                    "="
                    STRING |
                    NUMBER 
                    ";"                              

FunctionDeclaration -> "instr" IDENTIFIER "(" Parameter* ")" ReturnDeclaration BlockStatement ;
 
Parameter ->  (IDENTIFIER 
                  (String | Double | Boolean) 
                  \[,\]? 
              )*
              ;
                  
ReturnDeclaration -> \[: TYPE_DOUBLE, TYPE_STRING, TYPE_BOOLEAN\]                  
                                    
Statement -> 
            BlockStatement |
            ExpressionStatement |
            FunctionStatement |
            IfStatement |
            ReturnStatement |
            While-LoopStatement |
            For-LoopStatement | 
            VarStatement |
            ValStatement ;
            
BlockStatement -> "{" Statement* "}" ;
                    
ExpressionStatement -> Expression ";" ;


IfStatement ->  "if" "(" Expression ")" "{" Statement "}" \[ else "{" Statement "}" ] ;

ReturnStatement -> "return" ExpressionStatement ";"

WhileStatement -> "while" "(" Expression ")" "{" Statement "}"

Expression -> AssignmentExpression |
              BinaryExpression |
              CallExpression |
              GroupingExpression |
              LiteralExpression |
              SetExpression |  
              UnaryExpression |
              VariableExpression |
              ValueExpression ;
              
AssignmentExpression ->               
 





                    