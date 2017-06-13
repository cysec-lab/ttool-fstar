#! /bin/sh

#Define colors for success/error messages
RED='\e[0;31m'
GREEN='\e[0;32m'
BLUE='\e[0;34m'
BLACK='\e[0;30m'
DARKGRAY='\e[1;30m'
LIGHTBLUE='\e[1;34m'
GREEN='\e[0;32m'
LIGHTGREEN='\e[1;32m'
CYAN='\e[0;36m'
LIGHTCYAN='\e[1;36m'
RED='\e[0;31m'
LIGHTRED='\e[1;31m'
PURPLE='\e[0;35m'
LIGHTPURPLE='\e[1;35m'
BROWNORANGE='\e[0;33m'
YELLOW='\e[1;33m'
LIGHTGRAY='\e[0;37m'
WHITE='\e[1;37m'
NC='\e[0m'
#Locate javacc on the machine
JAVACC=$(locate javacc.sh)

if [ -z $JAVACC ]; then echo -e "${RED}ERROR: javacc path not found${NC}"; exit; fi

rm -r parser
mkdir parser
cp CPparser.jjt parser/
#cp ASTID.java parser/
cp ParseException.java parser/
#cp UniqueException.java parser/
#cp Token.java parser/
cd parser

#Invoke Javatree on the parser: DO IT ON THE .JJT FILE!
echo -e "${BLUE}Invoking jjtree on the parser .jjt...${NC}"
jjtree CPparser.jjt

#Invoke Javacc on the parser: DO IT ON THE .JJ FILE!
echo -e "${BLUE}Invoking javacc on the parser .jj...${NC}"
JAVACCcmd=$($JAVACC CPparser.jj)

#Test for errors when building the parser
GREPcmd1=$(echo $JAVACCcmd | grep -c '0 errors')
GREPcmd2=$(echo $JAVACCcmd | grep -c 'successfully')
if [ $GREPcmd = '1'] || [ $GREPcmd2 = '1' ]; then 
	echo $JAVACCcmd; 
	echo -e "${GREEN}SUCCESS: press ENTER to continue...${NC}";
	read;
#else
#	echo $JAVACCcmd; 
#	echo -e "${RED}ERROR(S): exiting script!${NC}"
#	exit;	
fi

cd ..
#Compile the Java classes generated by the previous command
echo -e "${BLUE}Compiling .java classes...${NC}"
javac -classpath ../../:parser parser/*.java

#Test the parser on a sample file
echo -e "${BLUE}Launching the parser...${NC}"
java -classpath ./parser/:../../ CPparser < ./spec2.tmlcp
#java -classpath ./parser/:../../ CPparser < ./spec.tmlcp
#./HOC2.test