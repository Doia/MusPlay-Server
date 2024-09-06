# MusPlay
Repositorio app MusPlay backend desarrollado en SpringBoot


Como hacer funcionar el servidor.

1. introducir comando: "cd musPlay-Server/springboot-crud"
2. Si es la primera vez introducir comando: "mvn clean install"
3. Instalar todo los que nos pida.

4. El resto de veces introducir comando: "mvn spring-boot:run"


Instalaciones necesarias antes de correr la aplicacion.

0. Java LTS 17

1. MAVEN: 
    1. Dirígete a https://maven.apache.org/download.cgi
    2. Descarga un Apache-maven.zip
    3. Extraer el archivo descargado:
    4. Descomprime el archivo descargado en una ubicación de tu elección. Por ejemplo, podrías extraerlo a C:\Program Files\Apache\maven en Windows o a /usr/local/apache-maven en     Linux/macOS.

    5. Configurar las variables de entorno:

        Windows:
        Abre el Panel de Control y busca "Variables de entorno".
        En "Variables del sistema", haz clic en "Nueva" y crea una variable llamada M2_HOME con la ruta de la carpeta donde descomprimiste Maven.
        Agrega %M2_HOME%\bin a la variable Path. 

2. BBDD
    1. Instalar BBDD mySql
    2. abrir mysql: "mysql -u root"
    3. crear la base de datos y el usuario: 
        "CREATE DATABASE musplay_bbdd;"
        "ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';"




1. introducir comando: "cd musPlay-Server/springboot-crud"
2. Si es la primera vez introducir comando: "mvn clean install"
3. Instalar todo los que nos pida.

4. El resto de veces introducir comando: "mvn spring-boot:run"

Tras instalar todo deberia funcionar.

//pc corporativo en powershell

$env:JAVA_HOME="C:\Users\diego.demiguel\java\jdk-17.0.12"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
./mvnw spring-boot:run