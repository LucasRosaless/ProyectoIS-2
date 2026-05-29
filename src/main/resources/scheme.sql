-- r:\Rosales House\Lucas UNI\ISI 2\ProyectoIS-2\src\main\resources\scheme.sql

-- Elimina las tablas en orden inverso para evitar conflictos de Foreign Keys
DROP TABLE IF EXISTS Asignacion_Docente;
DROP TABLE IF EXISTS Nota;
DROP TABLE IF EXISTS Inscripcion;
DROP TABLE IF EXISTS Catedra;
DROP TABLE IF EXISTS Correlativas_previas;
DROP TABLE IF EXISTS Materia;
DROP TABLE IF EXISTS Plan_estudio;
DROP TABLE IF EXISTS Carrera;
DROP TABLE IF EXISTS Administrador;
DROP TABLE IF EXISTS Profesor;
DROP TABLE IF EXISTS Alumno;
DROP TABLE IF EXISTS Persona;
DROP TABLE IF EXISTS TipoProfesor;
DROP TABLE IF EXISTS TipoNota;
DROP TABLE IF EXISTS PeriodoAcademico;
DROP TABLE IF EXISTS EstadoInscripcion;
DROP TABLE IF EXISTS TipoAlumno;

-- Creación de Tablas de Referencia (Enumeraciones)
CREATE TABLE TipoAlumno (
    tipo VARCHAR(20) PRIMARY KEY
);
INSERT INTO TipoAlumno (tipo) VALUES ('INGRESANTE'), ('AVANZADO');

CREATE TABLE EstadoInscripcion (
    estado VARCHAR(20) PRIMARY KEY
);
INSERT INTO EstadoInscripcion (estado) VALUES ('EN_CURSADA'), ('REGULAR'), ('APROBADA'), ('LIBRE');

CREATE TABLE PeriodoAcademico (
    periodo VARCHAR(20) PRIMARY KEY
);
INSERT INTO PeriodoAcademico (periodo) VALUES ('CUATRIMESTRAL'), ('BIMESTRAL'), ('ANUAL');

CREATE TABLE TipoNota (
    tipo VARCHAR(20) PRIMARY KEY
);
INSERT INTO TipoNota (tipo) VALUES ('PARCIAL'), ('FINAL'), ('TP');

CREATE TABLE TipoProfesor (
    tipo VARCHAR(20) PRIMARY KEY
);
INSERT INTO TipoProfesor (tipo) VALUES ('RESPONSABLE'), ('JEFE_PRACTICO'), ('AYUDANTE');

-- Tablas Principales (Entidades)
CREATE TABLE Persona (
    dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    attribute3 VARCHAR(100), -- Atributo genérico del diagrama
    telefono VARCHAR(50),
    correo VARCHAR(100),
    fecha_nacimiento DATE,
    user_login VARCHAR(50),
    pass_login VARCHAR(50)
);

CREATE TABLE Alumno (
    legajo INT PRIMARY KEY,
    dni_persona VARCHAR(20) REFERENCES Persona(dni),
    tipo_alumno VARCHAR(20) REFERENCES TipoAlumno(tipo)
);

CREATE TABLE Profesor (
    legajo_docente VARCHAR(50) PRIMARY KEY,
    dni_persona VARCHAR(20) REFERENCES Persona(dni)
);

CREATE TABLE Administrador (
    dni_persona VARCHAR(20) PRIMARY KEY REFERENCES Persona(dni),
    cargo_administrative VARCHAR(100)
);

CREATE TABLE Carrera (
    id_carrera INT PRIMARY KEY,
    codigo INT,
    nombre VARCHAR(100),
    duracion_anios INT
);

CREATE TABLE Plan_estudio (
    id_plan INT PRIMARY KEY,
    resolucion VARCHAR(100),
    anio_vigencia INT,
    estado INT,
    id_carrera INT NOT NULL REFERENCES Carrera(id_carrera)
);

CREATE TABLE Materia (
    id_materia INT PRIMARY KEY,
    codigo VARCHAR(20),
    nombre VARCHAR(100),
    periodo VARCHAR(20) REFERENCES PeriodoAcademico(periodo),
    id_plan INT NOT NULL REFERENCES Plan_estudio(id_plan)
);

-- Relación recursiva (Correlativas)
CREATE TABLE Correlativas_previas (
    id_materia INT REFERENCES Materia(id_materia),
    id_materia_correlativa INT REFERENCES Materia(id_materia),
    PRIMARY KEY (id_materia, id_materia_correlativa)
);

CREATE TABLE Catedra (
    id_catedra INT PRIMARY KEY,
    anio INT,
    comision INT,
    id_materia INT NOT NULL REFERENCES Materia(id_materia)
);

-- Clases Asociativas y Relaciones Muchos a Muchos
CREATE TABLE Inscripcion (
    id_inscripcion INT PRIMARY KEY,
    fecha_inscripcion INT,
    estado_inscripcion VARCHAR(20) REFERENCES EstadoInscripcion(estado),
    legajo_alumno INT NOT NULL REFERENCES Alumno(legajo),
    id_catedra INT NOT NULL REFERENCES Catedra(id_catedra)
);

CREATE TABLE Nota (
    id_nota INT PRIMARY KEY,
    valor INT,
    tipo_nota VARCHAR(20) REFERENCES TipoNota(tipo),
    fecha INT,
    id_inscripcion INT NOT NULL REFERENCES Inscripcion(id_inscripcion)
);

CREATE TABLE Asignacion_Docente (
    legajo_docente VARCHAR(50) REFERENCES Profesor(legajo_docente),
    id_catedra INT REFERENCES Catedra(id_catedra),
    rol VARCHAR(20) REFERENCES TipoProfesor(tipo),
    fecha_asignacion INT,
    PRIMARY KEY (legajo_docente, id_catedra)
);