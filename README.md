# SIGMA — Gestor de Tareas para Equipos de Programación
 
> Proyecto universitario para el curso **Programación Orientada a Objetos (ICC490-1)**  
> Universidad de La Frontera
 
---
 
## Descripción
 
**SIGMA** es una aplicación de escritorio desarrollada en Java que permite gestionar tareas dentro de equipos de programación. El sistema organiza el trabajo a través de **metas** (agrupaciones de tareas) y controla el acceso mediante tres roles de usuario distintos, cada uno con funcionalidades específicas.
 
---
 
## Integrantes
 
| Nombre | Rol en el equipo |
|---|---|
| Sebastián Colomera | Desarrollador |
| Sebastián Durán | Desarrollador |
| Felipe Rain | Desarrollador |
 
---
 
## Tecnologías utilizadas
 
- **Java 17**
- **Java Swing** — interfaz gráfica de escritorio
- **Maven** — gestión de dependencias y compilación
- **Gson 2.10.1** — serialización y deserialización JSON
- **JUnit 5 (Jupiter 5.10.1)** — pruebas unitarias
---
 
## Estructura del proyecto
 
```
sigma-project/
├── pom.xml
├── data/
│   ├── usuarios.json       # Datos persistentes de usuarios
│   └── metas.json          # Datos persistentes de metas y tareas
└── src/
    ├── main/java/sigma/
    │   ├── app/
    │   │   ├── Main.java           # Punto de entrada
    │   │   ├── GestorSigma.java    # Controlador central (lógica de negocio)
    │   │   └── SigmaConfig.java    # Constantes de rutas
    │   ├── modelo/
    │   │   ├── Usuario.java
    │   │   ├── Tarea.java
    │   │   ├── Meta.java
    │   │   ├── RolUsuario.java     # Enum: SUPERUSUARIO, LIDER, USUARIO
    │   │   └── EstadoTarea.java    # Enum: PENDIENTE, EN_PROCESO, POSTERGADA, FUERA_DE_PLAZO, COMPLETADA
    │   ├── persistencia/
    │   │   └── GestorJSON.java     # Lectura y escritura de archivos JSON
    │   └── vista/gui/
    │       ├── LoginFrame.java
    │       ├── MenuSuperusuarioFrame.java
    │       ├── MenuLiderFrame.java
    │       ├── MenuUsuarioFrame.java
    │       ├── GestionTareasPanel.java
    │       └── CambiarEstadoPanel.java
    └── test/java/sigma/
        ├── app/GestorSigmaTest.java
        ├── modelo/
        │   ├── MetaTest.java
        │   ├── TareaTest.java
        │   └── UsuarioTest.java
        └── persistencia/GestorJSONTest.java
```
 
---
 
## Arquitectura
 
El proyecto sigue una arquitectura de **3 capas**:
 
```
VISTA (sigma.vista.gui)
    └── usa ↓
CONTROLADOR (sigma.app)
    └── usa ↓
PERSISTENCIA (sigma.persistencia)
    └── lee/escribe ↓
data/*.json
 
sigma.modelo ← usado por todas las capas
```
 
---
 
## Roles de usuario
 
| Rol | Permisos |
|---|---|
| `SUPERUSUARIO` | Crear, editar y eliminar usuarios; resetear el sistema |
| `LIDER` | Crear metas, asignar tareas, ver el estado de todo el equipo |
| `USUARIO` | Ver sus tareas asignadas y cambiar el estado de las mismas |
 
---
 
## Requisitos previos
 
- **JDK 17** o superior instalado
- **Apache Maven 3.6+** instalado
- IDE recomendado: IntelliJ IDEA
---
 
## Instalación y ejecución
 
**1. Clonar el repositorio**
 
```bash
git clone https://github.com/<usuario>/sigma-project.git
cd sigma-project
```
 
**2. Compilar el proyecto**
 
```bash
mvn compile
```
 
**3. Ejecutar la aplicación**
 
```bash
mvn exec:java -Dexec.mainClass="sigma.app.Main"
```
 
O bien, ejecutar directamente `Main.java` desde el IDE.
 
---
 
## Credenciales por defecto
 
Al iniciar por primera vez, el sistema crea automáticamente un usuario administrador:
 
| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | SUPERUSUARIO |
 
---
 
## Pruebas
 
Para ejecutar el conjunto de pruebas unitarias:
 
```bash
mvn test
```
 
Los tests cubren las clases `GestorSigma`, `Meta`, `Tarea`, `Usuario` y `GestorJSON`.
 
---
 
## Persistencia de datos
 
Los datos se almacenan en archivos JSON dentro del directorio `data/`:
 
- `data/usuarios.json` — lista de usuarios registrados
- `data/metas.json` — metas y sus tareas asociadas
> **Nota:** estos archivos están excluidos del repositorio por `.gitignore`. El directorio `data/` se mantiene rastreado mediante el archivo `.gitkeep`.
 
---
 
## Licencia
 
Proyecto académico sin licencia comercial.  
Uso exclusivo para evaluación en el curso ICC490-1 — UFRO.
