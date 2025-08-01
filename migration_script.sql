-- Script de migración para agregar columna codigo_interno a la tabla producto
-- Ejecutar este script en PostgreSQL si hay problemas con la migración automática

-- Verificar si la columna existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'producto' 
        AND column_name = 'codigo_interno'
    ) THEN
        -- Crear la columna
        ALTER TABLE producto ADD COLUMN codigo_interno VARCHAR(255);
        
        -- Actualizar productos existentes con códigos temporales
        UPDATE producto SET codigo_interno = CONCAT('TEMP-', id) WHERE codigo_interno IS NULL;
        
        -- Hacer la columna NOT NULL después de llenarla
        ALTER TABLE producto ALTER COLUMN codigo_interno SET NOT NULL;
        
        -- Agregar constraint único
        ALTER TABLE producto ADD CONSTRAINT uk_codigo_interno UNIQUE (codigo_interno);
        
        RAISE NOTICE 'Columna codigo_interno creada exitosamente';
    ELSE
        RAISE NOTICE 'Columna codigo_interno ya existe';
    END IF;
END $$;

-- Verificar que la columna se creó correctamente
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'producto' 
AND column_name = 'codigo_interno'; 