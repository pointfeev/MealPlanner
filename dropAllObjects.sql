BEGIN
    dbms_output.enable();
    FOR object IN (SELECT object_name, object_type FROM user_objects)
        LOOP
            BEGIN
                IF object.object_type = 'TABLE'
                THEN
                    EXECUTE IMMEDIATE 'DROP '
                        || object.object_type
                        || ' "'
                        || object.object_name
                        || '" CASCADE CONSTRAINTS';
                ELSE
                    EXECUTE IMMEDIATE 'DROP '
                        || object.object_type
                        || ' "'
                        || object.object_name
                        || '"';
                END IF;
            EXCEPTION
                WHEN OTHERS
                    THEN
                        dbms_output.put_line('FAILED: DROP '
                            || object.object_type
                            || ' "'
                            || object.object_name
                            || '"'
                        );
            END;
        END LOOP;
    dbms_output.disable();
END;
/
PURGE RECYCLEBIN
/