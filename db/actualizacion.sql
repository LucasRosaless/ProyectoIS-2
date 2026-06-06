-- =============================================================================
-- Script de Limpieza y Población de Base de Datos - Sistema de Gestión Universitaria
-- =============================================================================

-- Desactivar temporalmente foreign keys para evitar conflictos de integridad al limpiar
PRAGMA foreign_keys = OFF;

-- 1. Limpieza total de las tablas principales y secundarias
DELETE FROM Asignacion_Docente;
DELETE FROM Nota;
DELETE FROM Inscripcion;
DELETE FROM Catedra;
DELETE FROM Correlativas_previas;
DELETE FROM Materia;
DELETE FROM Plan_estudio;
DELETE FROM Carrera;
DELETE FROM Administrador;
DELETE FROM Profesor;
DELETE FROM Alumno;
DELETE FROM Persona;
DELETE FROM users;

-- Reactivar foreign keys
PRAGMA foreign_keys = ON;

-- =============================================================================
-- 2. Inserción de Carreras (25 carreras según el listado)
-- =============================================================================
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (1, 1001, 'ANALISTA EN COMPUTACIÓN', 3);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (2, 1002, 'ANALISTA QUÍMICO', 3);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (3, 1003, 'LICENCIATURA EN CIENCIAS BIOLÓGICAS', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (4, 1004, 'LICENCIATURA EN CIENCIAS DE LA COMPUTACIÓN', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (5, 1005, 'LICENCIATURA EN FÍSICA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (6, 1006, 'LICENCIATURA EN GEOLOGÍA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (7, 1007, 'LICENCIATURA EN MATEMÁTICA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (8, 1008, 'LICENCIATURA EN QUÍMICA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (9, 1009, 'MICROBIOLOGÍA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (10, 1010, 'PROFESORADO EN CIENCIAS BIOLÓGICAS', 4);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (11, 1011, 'PROFESORADO EN CIENCIAS DE LA COMPUTACIÓN', 4);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (12, 1012, 'PROFESORADO EN FÍSICA', 4);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (13, 1013, 'PROFESORADO EN MATEMÁTICA', 4);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (14, 1014, 'PROFESORADO EN QUÍMICA', 4);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (15, 1015, 'TÉCNICO DE LABORATORIO', 3);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (16, 1016, 'INGENIERÍA ELECTRICISTA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (17, 1017, 'INGENIERÍA EN TELECOMUNICACIONES', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (18, 1018, 'INGENIERÍA MECÁNICA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (19, 1019, 'INGENIERÍA QUÍMICA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (20, 1020, 'INGENIERÍA EN ENERGÍAS RENOVABLES', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (21, 1021, 'CONTADOR PÚBLICO', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (22, 1022, 'LICENCIATURA EN ADMINISTRACIÓN', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (23, 1023, 'LICENCIATURA EN ECONOMÍA', 5);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (24, 1024, 'TECNICATURA EN GESTIÓN EMPRESARIAL', 3);
INSERT INTO Carrera (id_carrera, codigo, nombre, duracion_anios) VALUES (25, 1025, 'TECNICATURA EN GESTIÓN AGROPECUARIA Y AGROALIMENTARIA', 3);

-- =============================================================================
-- 3. Inserción de Planes de Estudio (Uno para cada carrera)
-- =============================================================================
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (1, 'Res. CD 001/20', 2020, 1, 1);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (2, 'Res. CD 002/20', 2020, 1, 2);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (3, 'Res. CD 003/20', 2020, 1, 3);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (4, 'Res. CD 004/20', 2020, 1, 4);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (5, 'Res. CD 005/20', 2020, 1, 5);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (6, 'Res. CD 006/20', 2020, 1, 6);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (7, 'Res. CD 007/20', 2020, 1, 7);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (8, 'Res. CD 008/20', 2020, 1, 8);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (9, 'Res. CD 009/20', 2020, 1, 9);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (10, 'Res. CD 010/20', 2020, 1, 10);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (11, 'Res. CD 011/20', 2020, 1, 11);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (12, 'Res. CD 012/20', 2020, 1, 12);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (13, 'Res. CD 013/20', 2020, 1, 13);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (14, 'Res. CD 014/20', 2020, 1, 14);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (15, 'Res. CD 015/20', 2020, 1, 15);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (16, 'Res. CD 016/20', 2020, 1, 16);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (17, 'Res. CD 017/20', 2020, 1, 17);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (18, 'Res. CD 018/20', 2020, 1, 18);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (19, 'Res. CD 019/20', 2020, 1, 19);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (20, 'Res. CD 020/20', 2020, 1, 20);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (21, 'Res. CD 021/20', 2020, 1, 21);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (22, 'Res. CD 022/20', 2020, 1, 22);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (23, 'Res. CD 023/20', 2020, 1, 23);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (24, 'Res. CD 024/20', 2020, 1, 24);
INSERT INTO Plan_estudio (id_plan, resolucion, anio_vigencia, estado, id_carrera) VALUES (25, 'Res. CD 025/20', 2020, 1, 25);

-- =============================================================================
-- 4. Inserción de Materias (Computación - Asignadas a Plan 1 y Plan 4)
-- =============================================================================
-- COMPUTACION - Plan 1 (Analista en Computación)
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1001, 'ICP1', 'Introducción a la Computación y Programación I', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1002, 'IM', 'Introducción a la Matemática', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1003, 'LRP', 'Lógica y Resolución de Problemas', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1004, 'ICP2', 'Introducción a la Computación y Programación II', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1005, 'MD', 'Matemática Discreta', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1006, 'EDA', 'Estructura de Datos y Algoritmos', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1007, 'OC', 'Organización de Computadoras', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1008, 'CS', 'Computación y Sociedad', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1009, 'ING1', 'Inglés I', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1010, 'ADA1', 'Análisis y Diseño de Algoritmos I', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1011, 'BD', 'Bases de Datos', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1012, 'IS1', 'Ingeniería de Software I', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1013, 'ING2', 'Inglés II', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1014, 'PLP', 'Paradigmas y Lenguajes de Programación', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1015, 'IS2', 'Ingeniería de Software II', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1016, 'SOR', 'Sistemas Operativos y Redes', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1017, 'SD', 'Sistemas Distribuidos', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1018, 'SRI', 'Seminario de Redacción Informativa', 'CUATRIMESTRAL', 1);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (1019, 'OPT1', 'Asignatura Optativa', 'CUATRIMESTRAL', 1);

-- COMPUTACION - Plan 4 (Licenciatura en Ciencias de la Computación)
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4001, 'ICP1', 'Introducción a la Computación y Programación I', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4002, 'IM', 'Introducción a la Matemática', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4003, 'LRP', 'Lógica y Resolución de Problemas', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4004, 'ICP2', 'Introducción a la Computación y Programación II', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4005, 'MD', 'Matemática Discreta', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4006, 'EDA', 'Estructura de Datos y Algoritmos', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4007, 'OC', 'Organización de Computadoras', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4008, 'CS', 'Computación y Sociedad', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4009, 'ING1', 'Inglés I', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4010, 'ADA1', 'Análisis y Diseño de Algoritmos I', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4011, 'BD', 'Bases de Datos', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4012, 'IS1', 'Ingeniería de Software I', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4013, 'ING2', 'Inglés II', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4014, 'PLP', 'Paradigmas y Lenguajes de Programación', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4015, 'IS2', 'Ingeniería de Software II', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4016, 'SOR', 'Sistemas Operativos y Redes', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4017, 'SD', 'Sistemas Distribuidos', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4018, 'SRI', 'Seminario de Redacción Informativa', 'CUATRIMESTRAL', 4);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (4019, 'OPT1', 'Asignatura Optativa', 'CUATRIMESTRAL', 4);

-- =============================================================================
-- 5. Inserción de Materias (Física - Asignadas a Plan 5 y Plan 12)
-- =============================================================================
-- FISICA - Plan 5 (Licenciatura en Física)
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5001, 'MAT1', 'Matemática I', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5002, 'IF', 'Introducción a la Física', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5003, 'TPD', 'Taller de problematización docente', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5004, 'ING1', 'Inglés I', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5005, 'QG', 'Química General', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5006, 'MAT2', 'Matemática II', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5007, 'FG', 'Física General', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5008, 'IE', 'Instituciones Educativas', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5009, 'IFQ', 'Introducción a la Fisicoquímica', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5010, 'PE', 'Pedagogía Especial', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5011, 'MAT3', 'Matemática III', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5012, 'SE', 'Sociología de la Educación', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5013, 'F1', 'Física I', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5014, 'IPD1', 'Iniciación a la Práctica Docente', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5015, 'PEV', 'Psicología Evolutiva', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5016, 'IE', 'Investigación Educativa', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5017, 'ING2', 'Inglés II', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5018, 'F2', 'Física II', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5019, 'EPE', 'Elementos de Psicología Educacional', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5020, 'IPD2', 'Iniciación a la Práctica Docente II', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5021, 'PFGA', 'Principios físicos de Geología y Astronomía', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5022, 'F3', 'Física III', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5023, 'DID', 'Didáctica', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5024, 'F4', 'Física IV', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5025, 'PROY1', 'Proyecto I', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5026, 'PDC1', 'Práctica Docente y Currículo I', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5027, 'TDF', 'Taller Didáctica de la Física', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5028, 'OPT1', 'Asignaturas Optativas', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5029, 'TEF', 'Tópicos Especiales de Física', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5030, 'EHF', 'Epistemología e Historia de la Física', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5031, 'PROY2', 'Proyecto II', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5032, 'PDC2', 'Práctica Docente y Currículo II', 'CUATRIMESTRAL', 5);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (5033, 'OPT2', 'Asignaturas Optativas II', 'CUATRIMESTRAL', 5);

-- FISICA - Plan 12 (Profesorado en Física)
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12001, 'MAT1', 'Matemática I', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12002, 'IF', 'Introducción a la Física', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12003, 'TPD', 'Taller de problematización docente', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12004, 'ING1', 'Inglés I', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12005, 'QG', 'Química General', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12006, 'MAT2', 'Matemática II', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12007, 'FG', 'Física General', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12008, 'IE', 'Instituciones Educativas', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12009, 'IFQ', 'Introducción a la Fisicoquímica', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12010, 'PE', 'Pedagogía Especial', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12011, 'MAT3', 'Matemática III', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12012, 'SE', 'Sociología de la Educación', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12013, 'F1', 'Física I', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12014, 'IPD1', 'Iniciación a la Práctica Docente', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12015, 'PEV', 'Psicología Evolutiva', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12016, 'IE', 'Investigación Educativa', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12017, 'ING2', 'Inglés II', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12018, 'F2', 'Física II', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12019, 'EPE', 'Elementos de Psicología Educacional', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12020, 'IPD2', 'Iniciación a la Práctica Docente II', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12021, 'PFGA', 'Principios físicos de Geología y Astronomía', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12022, 'F3', 'Física III', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12023, 'DID', 'Didáctica', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12024, 'F4', 'Física IV', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12025, 'PROY1', 'Proyecto I', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12026, 'PDC1', 'Práctica Docente y Currículo I', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12027, 'TDF', 'Taller Didáctica de la Física', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12028, 'OPT1', 'Asignaturas Optativas', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12029, 'TEF', 'Tópicos Especiales de Física', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12030, 'EHF', 'Epistemología e Historia de la Física', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12031, 'PROY2', 'Proyecto II', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12032, 'PDC2', 'Práctica Docente y Currículo II', 'CUATRIMESTRAL', 12);
INSERT INTO Materia (id_materia, codigo, nombre, periodo, id_plan) VALUES (12033, 'OPT2', 'Asignaturas Optativas II', 'CUATRIMESTRAL', 12);

-- =============================================================================
-- 6. Inserción de Cátedras Habilitadas (Una comisión para cada Materia)
-- =============================================================================
INSERT INTO Catedra (id_catedra, anio, comision, id_materia) SELECT id_materia, 2026, 1, id_materia FROM Materia;

-- =============================================================================
-- 7. Usuarios por Defecto (Hashed Password: 'admin123' / 'admin123')
-- =============================================================================
-- Admin principal
INSERT INTO users (id, name, password, tipo_usuario) VALUES (1, 'admin', '$2a$10$uisFRfeG7xpWiTijPkeSsegLaCJHEdqAe2t8EdtT70LcyOOYy6YC2', 'administrador');
INSERT INTO Persona (dni, nombre, apellido, correo, user_login, pass_login) VALUES ('99999999', 'Admin', 'Sistema', 'admin@universidad.edu.ar', 'admin', 'admin123');
INSERT INTO Administrador (dni_persona, cargo_administrative) VALUES ('99999999', 'Administrador General');

-- Profesor principal
INSERT INTO users (id, name, password, tipo_usuario) VALUES (2, 'profesor', '$2a$10$uisFRfeG7xpWiTijPkeSsegLaCJHEdqAe2t8EdtT70LcyOOYy6YC2', 'profesor');
INSERT INTO Persona (dni, nombre, apellido, correo, user_login, pass_login) VALUES ('88888888', 'Juan', 'Docente', 'juan.docente@universidad.edu.ar', 'profesor', 'admin123');
INSERT INTO Profesor (legajo_docente, dni_persona) VALUES ('PROF001', '88888888');

-- Alumno principal (Asociado al Plan 1 - Analista en Computación)
INSERT INTO users (id, name, password, tipo_usuario) VALUES (3, 'alumno', '$2a$10$uisFRfeG7xpWiTijPkeSsegLaCJHEdqAe2t8EdtT70LcyOOYy6YC2', 'alumno');
INSERT INTO Persona (dni, nombre, apellido, correo, user_login, pass_login) VALUES ('77777777', 'Maria', 'Estudiante', 'maria.estudiante@universidad.edu.ar', 'alumno', 'admin123');
INSERT INTO Alumno (legajo, dni_persona, tipo_alumno, id_plan) VALUES (10001, '77777777', 'AVANZADO', 1);
