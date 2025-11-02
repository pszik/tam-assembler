grammar Tasm;

program: progElement+ EOF;

progElement
  : instruction                                                 # anonProgElement
  | LABEL ':' instruction                                       # labelledProgElement
  ;

instruction
  : 'LOAD' '(' n=NUMBER ')' d=offset '[' r=REGISTER ']'         # loadInstr
  | 'LOADA' d=offset '[' r=REGISTER ']'                         # loadaInstr
  | 'LOADI' '(' n=NUMBER ')'                                    # loadiInstr
  | 'LOADL' d=offset                                            # loadlInstr
  | 'LOADL' c=CHAR                                              # loadlChar
  | 'STORE' '(' n=NUMBER ')' d=offset '[' r=REGISTER ']'        # storeInstr
  | 'STOREI' '(' n=NUMBER ')'                                   # storeiInstr
  | 'CALL' '(' n=REGISTER ')' d=offset '[' r=REGISTER ']'       # callInstr
  | 'CALL' PRIMITIVE                                            # callPrimitiveInstr
  | 'CALL' lbl=LABEL                                            # callLabelInstr
  | 'CALLI'                                                     # calliInstr
  | 'RETURN' '(' n=NUMBER ')' d=NUMBER                          # returnInstr
  | 'PUSH' d=NUMBER                                             # pushInstr
  | 'POP' '(' n=NUMBER ')' d=NUMBER                             # popInstr
  | 'JUMP' d=offset '[' r=REGISTER ']'                          # jumpInstr
  | 'JUMP' LABEL                                                # jumpLabelInstr
  | 'JUMPI'                                                     # jumpiInstr
  | 'JUMPIF' '(' n=NUMBER ')' d=offset '[' r=REGISTER ']'       # jumpifInstr
  | 'JUMPIF' '(' n=NUMBER ')' lbl=LABEL                         # jumpifLabelInstr
  | 'HALT'                                                      # haltInstr
  ;

offset
  : NUMBER
  | NEGNUMBER
  ;

REGISTER
  : 'CB'
  | 'CT'
  | 'PB'
  | 'PT'
  | 'SB'
  | 'ST'
  | 'HB'
  | 'HT'
  | 'LB'
  | 'L1'
  | 'L2'
  | 'L3'
  | 'L4'
  | 'L5'
  | 'L6'
  | 'CP'
  ;

PRIMITIVE
  : 'id'
  | 'not'
  | 'and'
  | 'or'
  | 'succ'
  | 'pred'
  | 'neg'
  | 'add'
  | 'sub'
  | 'mult'
  | 'div'
  | 'mod'
  | 'lt'
  | 'le'
  | 'ge'
  | 'gt'
  | 'eq'
  | 'ne'
  | 'eol'
  | 'eof'
  | 'get'
  | 'put'
  | 'geteol'
  | 'puteol'
  | 'getint'
  | 'putint'
  | 'new'
  | 'dispose'
  ;

CHAR: '\'' '\u0020'..'\u007e' '\'';

fragment DIGIT: [0-9];

NUMBER: DIGIT+;
NEGNUMBER: '-' NUMBER;

fragment LBLHEAD: [a-zA-Z];
fragment LBLCHAR: [a-zA-Z0-9_];

LABEL: LBLHEAD LBLCHAR*;

COMMENT: ';' .*? '\n' -> skip;
WS: [ \t\r\n] -> skip;