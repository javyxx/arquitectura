# language: es
Característica: Login
  Quiero testear el login de usuario
  Tanto el correcto como el incorrecto

  Esquema del escenario: Distintas combinaciones de datos
    Dado el nombre de usuario "<usuario>" y contraseña "<contraseña>" 
    Cuando invoco la url "<url>" con el método "<method>" 
    Entonces el resultado debe ser un http status <status>

  Ejemplos:
    | usuario   | contraseña | method | url    | status |
    | demo      | abcd123    | POST   | login  | 200    |
    | user      | nuevo      | POST   | login  | 401    |
    | nuevo     | nuevo      | POST   | login  | 401    |
    |  	        | 	         | POST   | login  | 401    |
    
  Escenario: Login con post empty
	Cuando invoco la url "login" con el método "POST" 
    Entonces el resultado debe ser un http status 401
