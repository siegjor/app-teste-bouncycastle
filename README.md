# Aplicativo para teste da biblioteca Bouncy Castle no Flutter

Aplicativo feito para testar o uso da biblioteca Bouncy Castle dentro do Flutter, através de platform channels.

O aplicativo possui um certificado e a assinatura da mensagem "1234567890". Ele irá receber no input uma mensagem do usuário, e irá verificar se a assinatura é válida para a mensagem inserida, retornando 'true' se for válida e 'false' caso contrário.

# Arquivos importantes

### android\app\src\main\java\com\example\testing_java_code\MainActivity.java
Contém o código Java usado para interagir com o código Dart através de platform channels.

### android\app\src\main\java\com\example\testing_java_code\VerifyEd448Signature.java
Contém as funções usadas para verificar a assinatura e para realizar a leitura dos arquivos necessários.

### android/app/src/main/assets
Contém o certificado, a assinatura e a chave privada (não utilizada no código).

### android/libs
Contém os arquivos .jar do Bouncy Castle, bcprov-jdk15to18-169.jar e bcpkix-jdk15on-169.jar

# Bibliotecas
A única biblioteca de terceiros utilizada é a do Bouncy Castle, instalada por meio de seu arquivo .jar

# Como executar o projeto
Para executar o projeto é necessário ter o ambiente de desenvolvimento Flutter instalado em sua máquina

[Montando o ambiente de desenvolvimento Flutter](https://flutter.dev/docs/get-started/install)

```
    git clone https://github.com/siegjor/app-teste-bouncycastle.git
    cd ./app-teste-bouncycastle-main
    flutter run
```