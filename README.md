# LAB04-AREP

### Autor: Juan Camilo Rojas Castro

## Desafió
1. El servicio MongoDB es una instancia de MongoDB corriendo en un container de docker en una máquina virtual de EC2
2. LogService es un servicio REST que recibe una cadena, la almacena en la base de datos y responde en un objeto JSON con las 10 ultimas cadenas almacenadas en la base de datos y la fecha en que fueron almacenadas.
3. La aplicación web APP-LB-RoundRobin está compuesta por un cliente web y al menos un servicio REST. El cliente web tiene un campo y un botón y cada vez que el usuario envía un mensaje, este se lo envía al servicio REST y actualiza la pantalla con la información que este le regresa en formato JSON. El servicio REST recibe la cadena e implementa un algoritmo de balanceo de cargas de Round Robin, delegando el procesamiento del mensaje y el retorno de la respuesta a cada una de las tres instancias del servicio LogService.

![](https://media.discordapp.net/attachments/691077311965167718/1032807458554527744/unknown.png)

## Solución

### Resumen
Este proyecto consiste en una arquitectura cliente-servidor el cual realiza peticiones, el servidor debe manejar estas 
peticiones, balanceando así la carga en 3 instancias del servicio encargado de consultar en la base de datos de mongo.

### Arquitectura

    UrlBase: http://ec2-3-82-206-61.compute-1.amazonaws.com/

#### cliente-servidor
Aquí tenemos el servidor Round Robin, con sus archivos estáticos y encargado de recibir las peticiones http del cliente

![](https://media.discordapp.net/attachments/584593411567517710/1032812474031951882/unknown.png)

Por otro lado tenemos el LogService, encargado de recibir las peticiones de RoundRobin y consultar la base de datos.

#### Round Robin

Este es el funcionamiento general de Round Robin, la BASE_URL debemos cambiarla por el localhost en caso de querer hacer
pruebas en local (si se hacen pruebas con Docker en local usar la ip de la máquina ex. 192.168.0.X) también tener en cuenta el puerto.

![](https://media.discordapp.net/attachments/584593411567517710/1032816814054461470/unknown.png)

Las peticiones que se realizan desde el cliente se hacen a /string y se procesan en la función de get y post.

#### Log Service

Log service va a recibir las peticiones hechas desde RoundRobin con la url base concatenado con el índice del número *roundrobin* para definir 
la última parte del puerto (URL:3500 + *roundrobin*, /query).

Con esto ya dependiendo si es get o post hace uso de la conexión con mongo y devuelve la respuesta:

![](https://media.discordapp.net/attachments/584593411567517710/1032832751595954278/unknown.png)

#### Docker

Para correr el proyecto con docker está el archivo de docker-compose ya configurado, solo queda correrlo:

    docker-compose up -d

Con esto ya quedaría la aplicación desplegada, aunque si no cambiamos la URL_BASE de RoundRobin y en la conexión con mongo 
nos conectaremos con los contenedores de la máquina virtual en EC2 y no con los contenedores locales.

![](https://media.discordapp.net/attachments/584593411567517710/1032839987785367592/unknown.png?width=1440&height=483)

### PRUEBAS EN EL DESPLIEGUE AWS

Ahora para poder correr el proyecto en la máquina virtual necesitamos tener las imágenes y correrlas, para esto
con docker compose ya tenemos las imágenes solo queda subirlas a dockerhub:

    docker tag lab04-arep-back1 juanrojascastr/lab04-arep:back
    docker tag lab04-arep-front juanrojascastr/lab04-arep:front

    docker push juanrojascastr/lab04-arep:back
    docker push juanrojascastr/lab04-arep:front

Ahora conectados a la máquina virtual debemos usar los siguientes comandos para traer y correr las imágenes:

    docker run -d -p 27017:27017 mongo:3.6.1
    docker run -d -p 80:8000 juanrojascastr/lab04-arep:front
    docker run -d -p 35000:8000 juanrojascastr/lab04-arep:back
    docker run -d -p 35001:8000 juanrojascastr/lab04-arep:back
    docker run -d -p 35002:8000 juanrojascastr/lab04-arep:back


Como vemos es solo ingresar a la url base del proyecto:

![](https://media.discordapp.net/attachments/584593411567517710/1032849535485087744/unknown.png)

![](https://media.discordapp.net/attachments/584593411567517710/1032849711150923816/unknown.png)