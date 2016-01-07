# Microservicios con Spring Boot (1/4)
### Introdución a Spring boot:
Spring boot es una plataforma que pretende simplificar el desarrollo y para ello realiza auto-configuración (convención en vez de configuración) de los componente. Usa Spring JavaConfig y se puede configurar toda la plataforma sin necesidad de usar XML.   

Las aplicaciones de spring boot son muy sencillas:

	import org.springframework.boot.*;
	import org.springframework.boot.autoconfigure.*;
	import org.springframework.web.bind.annotation.*;

	@SpringBootApplication
	public class Example {
		public static void main(String[] args) throws Exception {
			SpringApplication.run(Example.class, args);
		}
	}
### Estructura del código:

Ateniéndonos a la estructura hay tres formas de organizar el código de los micro-servicios.

  - Projectos separados
  - Un solo proyecto multi-módulo  
  - Un solo proyecto sin módulos 

La elección de uno u otro depende del grado de acoplamiento de los micro-servicios.

Por norma general si los micro-servicios no son dependiente entre ellos se debe de optar por crear proyectos separados ya que de esta forma se consigue una mayor flexibilidad y pueden diferir en cuanto a configuración sin que afecten a otros micro-servicios .

Si por el contrario son servicios con dependencias a nivel de lógica de aplicación es recomendable agruparlos en un proyecto multi-módulo de esta forma nos permite de un vistazo identificar si la modificación de un micro-servicios puede afectar a los micro-servicios de los que depende. 

Por último Spring boot permite tener varios servicios dentro del mismo proyecto pudiendo iniciarlos por separado , este caso es adecuado cuando los micro-servicios tiene dependencia a nivel de lógica de aplicación y también a nivel técnico ya que van a compartir las mismas librerías y probablemente la misma base de datos. 

Para esta serie de articulos se va a proceder a crear en un proyecto aparte un componente de arquitectura y luego un proyecto multi-módulo para los servicios concretos.

### Componente de arquitectura:

Para simplificar el desarrollo se ha creado un componente de arquitectura de forma que sea mas sencillo desarrollar los micro-servicios, este componente resuelve el tema de la seguridad, la configuración de los websockets , cache de datos, se puede configurar el accesos al modelo de usuario con jdbc o mongo, etc. 
Esto facilita el desarrollo evitando la repetición de código.

##### Uso del componente de arquitectura


```sh
$ git clone https://git.paradigmadigital.com/git/ArquitecturaMicroServiciosSprinBoot.git arquitecture
$ cd arquitecture
$ gradle bootRun
```

##### Desarrollo con el componente de arquitectura

Para crear un servicio nuevo usando la arquitectura creada es tan sencillo como sustituir la anotación @SpringBootApplication por @ParadigmaApplication, se puede configurar el tipo de accesso para recuperar los datos de usuario. Por defecto esta indicado el accesso en memoría pero para cambiarlo por el accesso a mongodb hay que indicar @ParadigmaApplication(accessDataMode=AccessDataMode.MONGO)


La seguridad se basa en anotaciones @PreAuthorize y @PostFilter:

    @PreAuthorize("hasRole('admin') && @permissions.allowAny('model', 'read')")
    @PostFilter("@permissions.allow(filterObject.id, 'model', 'read')")
    public List<Model> getAll() {
        ...
    }


Esta definido un bean en Spring llamado permissions que se encarga de evaluar si se dispone de permisos para una entidad e id dados. 
Se puede adaptar la nomeclatura a cada caso, ej:

	@permissions.allow(filterObject.id, 'es.paradigma.inditex.factura', 'imprimir')
	@permissions.allow(filterObject.id, 'es.paradigma.inditex.albaran', 'facturar')
	@permissions.allow(filterObject.id, 'es.paradigma.inditex.prenda', 'mover')

Para autenticarse tan solo hay que hacer login sobre la url http://localhost:8080/api/v1/login con el body {"demo", "abcd123"}
esto nos devuelve un head "X-AUTH-TOKEN" con el accessToken para poder hacer invocaciones en todos los servicios del proyecto.


### Projectos multimodulo 
En este proyecto multi-modulo (git url) se dispondra de 2 microservicios básicos los cuales no comparten código ni nada entre ellos , la razón es que se desea evitar que haya una dependencia directa entre ellos , queremos que esten complementamente desacoplados. 
Se ha creado un proyecto para compartir código que es importado por ambos proyectos en el que se puede compartir clases si es necesario la idea es usarlo para compartir interfaces o clases de utilidad pero siempre evitando el acoplamiento de la implementación de cada servicio.
En nuestro caso se usará para crear un modelo de datos independiente de cada servicio que permita comunicar ambos servicios a traves de un bus de eventos , los eventos representados por clases java están en este proyecto común .
También se dispone de un servicio que importa todos los demas para permitir agrupar todos los servicios en uno solo y desplegarlo en local en un solo servidor para pruebas y desarrollo. 

##### Seguridad de cada microservicio 
Para la implementar la seguridad se hace uso de JWT (Javascript Web Token) se ha usado ya que es un estandar abierto (RFC 7519) ademas de ser muy flexible y extensible ya que puede contener un conjunto flexible de datos como por ejemplo fecha de expiración, creador del token, fecha de creación, id para revocación y todos los datos que consideremos utiles para nuestro servicio.
 
Al poder incluir datos aleatorios podemos crear un token con los permisos del usuario incluidos dentro del propio token.
Esto nos permite crear un token con un subconjunto reducido de permisos para compartir el acceso a determinadas partes del api por aplicaciones externas de forma segura.También permite establecer la vigencia de ese token y todo esto sin necesidad de tener extructuras de datos propias para almacenar esa información.

Otra ventaja adicional es que se puede tener centralizado en un solo servicio la creación de los token de acceso 
y los demás servicios son capaces de evaluar por si mismos si tiene permisos de acceso si necesidad de tener visibilidad del servicio de creación de token ni de la base de datos usada para crear esos token.

La única consideración de seguridad a tener en cuenta es que estos servicios comparten una clave secreta que debe ser protegida para que nadie ajeno a la organización pueda usarla para crear tokens validos falsos. 

##### Comunicación entre microservicios
Para la comunicación de eventos entre los distintos microservicios se hace uso de un bus de eventos y del patrón comander para reacionar a estos eventos lo que permite ejecutar acciones de forma asincrona . 
Si por ejemplo tenemos replicada la información de usuario dentro del producto y esta información cambia podemos  avisar de forma asincrona a los demas servicios para que actualicen la información que tienen replicada. 
También nos permite realizar operaciones de borrado como por ejemplo borrar todos los productos pertenecientes a un usuario una vez ese usuario ha sido borrado. 
=======
Para configurar el acceso a la base de datos que se usa para validar los usuarios se hace uso del archivo arquitectura.yml en donde
se guardan todas las propiedades de configuración de la arquitectura común de microservicios.

### Todo's
- Proyecto multimodulo con Gradle
- Integrar Spring Cloud y servicio de agragación
- Automatización con DockerCompose

### Version
1.0.0

License
----
GPL

Autor
----
Javier Ledo Vázquez, Email: <jledo@paradigmatecnologico.com>


[git-repo-url]: http://git.paradigmatecnologico.com/arquitecture.git