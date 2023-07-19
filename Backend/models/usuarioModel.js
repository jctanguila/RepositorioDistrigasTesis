const db = require('../config/config');
const bcrypt = require('bcryptjs')

const Usuario = {};

Usuario.listaUsuarios = (req, result) => {
    const sql = `SELECT
    usuarios.id,
	roles.rol, 
	personas.nombres, 
	personas.apellidos, 
	personas.cedula, 
	personas.telefono, 
	personas.direccion, 
	personas.email, 
	personas.estado 
FROM
	usuarios
	INNER JOIN
	roles
	ON 
		usuarios.idRol = roles.id
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
	WHERE usuarios.estado = "ACTIVO" AND usuarios.idRol != 3`
    db.query(sql, (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, res)
        }
    })
}

Usuario.listaRepartidores = (req, result) => {
    const sql = `SELECT
	usuarios.id, 
	roles.rol, 
	personas.nombres, 
	personas.apellidos, 
	personas.cedula, 
	personas.telefono, 
	personas.direccion, 
	personas.email, 
	personas.estado, 
	repartidores.lat, 
	repartidores.lng
FROM
	usuarios
	INNER JOIN
	roles
	ON 
		usuarios.idRol = roles.id
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
	INNER JOIN
	repartidores
	ON 
		usuarios.id = repartidores.idUsuario
WHERE
	usuarios.estado = "ACTIVO" AND
	usuarios.idRol = 4`
    db.query(sql, (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, res)
        }
    })
}

Usuario.listaClientes = (req, result) => {
    const sql = `SELECT
    usuarios.id,
	roles.rol, 
	personas.nombres, 
	personas.apellidos, 
	personas.cedula, 
	personas.telefono, 
	personas.direccion, 
	personas.email, 
	personas.estado
FROM
	usuarios
	INNER JOIN
	roles
	ON 
		usuarios.idRol = roles.id
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
	WHERE usuarios.estado = "ACTIVO" AND usuarios.idRol =3`
    db.query(sql, (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, res)
        }
    })
}

Usuario.FindByUsername = (username, result) => {
    const sql = `SELECT
	personas.nombres, 
	personas.apellidos, 
	personas.cedula, 
	personas.telefono, 
	personas.direccion, 
	personas.email, 
	usuarios.id as idUsuario, 
	usuarios.usuario, 
	usuarios.clave, 
	usuarios.estado, 
	roles.rol, 
	personas.id as idPersona, 
	roles.id as idRol
    FROM
	usuarios
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
	INNER JOIN
	roles
	ON 
		usuarios.idRol = roles.id
    WHERE
	usuario=?`
    db.query(sql, [username], (err, res) => {
        if (err) {
            console.log(err)
            result(err, null)
        } else {
            console.log("usuario:", res[0])
            result(null, res[0])
        }
    })
}

Usuario.FindById = (id, result) => {
    const sql = 'SELECT usuarios.*, roles.rol FROM usuarios	INNER JOIN roles ON usuarios.idRol = roles.id WHERE usuarios.id=?'
    db.query(sql, [id], (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, res[0])
        }
    })
}

Usuario.tomarRol = (datos, result) => {
    const sql = 'SELECT usuarios.*, roles.rol FROM usuarios	INNER JOIN roles ON usuarios.idRol = roles.id WHERE usuarios.id=? AND roles.rol=?'
    db.query(sql, [datos.id, datos.rol], (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, res[0])
        }
    })
}

Usuario.actPass = (datos, result) => {

    var sql = 'SELECT * FROM usuarios WHERE id=?'
    db.query(sql, [datos.idUsuario], async (err, res) => {
        if (err) {
            console.log(err)
            result(err, null)
        } else {
            var usuario = res[0]
            const isPasswordValid = await bcrypt.compare(datos.claveOriginal, usuario.clave)

            if (isPasswordValid) {
                var sql = 'UPDATE usuarios SET clave=? WHERE id=?'
                const hash = await bcrypt.hash(datos.clave, 10);
                db.query(
                    sql,
                    [
                        hash,
                        datos.idUsuario
                    ], (err, res) => {
                        if (err) {
                            result(err, null)
                        }
                    })
                result(null, datos)
            } else {
                result(err, null)
            }
        }
    })
}

Usuario.actPerfil = (datos, result) => {
    var sql = 'SELECT * FROM usuarios WHERE id=?'
    db.query(sql, [datos.idUsuario], (err, res) => {
        if (err) {
            result(err, null)
        } else {
            var usuario = res[0]
            sql = 'UPDATE personas SET nombres=?, apellidos=?, telefono=?, direccion=?, email=? WHERE id=?'
            db.query(
                sql,
                [
                    datos.nombres,
                    datos.apellidos,
                    datos.telefono,
                    datos.direccion,
                    datos.email,
                    usuario.idPersona
                ], (err, res) => {
                    if (err) {
                        console.log(err)
                        result(err, null)
                    }
                })
            result(null, datos)
        }
    })
}

Usuario.actUsuario = (datos, result) => {
    Usuario.actPerfil(datos, (err, data) => {
        if (err) {
            console.log(err)
        }
    })
    var idRol = 0
    if (datos.rol == "Administrador") {
        idRol = 1
    } else if (datos.rol == "Empleado") {
        idRol = 2
    } else if (datos.rol == "Cliente") {
        idRol = 3
    } else if (datos.rol == "Repartidor") {
        idRol = 4
    }
    sql = 'UPDATE usuarios SET idRol=? WHERE id=?'
    db.query(sql, [idRol, datos.idUsuario], (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, datos)
        }
    })
}

Usuario.eliUsuario = (datos, result) => {
    // 1. Eliminar pedidos del usuario
    let sql = 'DELETE FROM pedidos WHERE idCliente=?';
    db.query(sql, [datos.idUsuario], (err, res) => {
        if (err) {
            console.log(err);
            result(err, null);
            return;
        }
        // 2. Eliminar de clientes
        sql = 'DELETE FROM clientes WHERE idUsuario=?';
        db.query(sql, [datos.idUsuario], (err, res) => {
            if (err) {
                console.log(err);
                result(err, null);
                return;
            }
            // 3. Buscar información del usuario para obtener idPersona
            sql = 'SELECT * FROM usuarios WHERE id=?';
            db.query(sql, [datos.idUsuario], (err, res) => {
                if (err) {
                    console.log(err);
                    result(err, null);
                    return;
                }
                var usuario = res[0];
                // 4. Primero, eliminar el usuario
                sql = 'DELETE FROM usuarios WHERE id=?';
                db.query(sql, [datos.idUsuario], (err, res) => {
                    if (err) {
                        console.log(err);
                        result(err, null);
                        return;
                    }
                    // 5. Luego, eliminar de personas
                    sql = 'DELETE FROM personas WHERE id=?';
                    db.query(sql, [usuario.idPersona], (err, res) => {
                        if (err) {
                            console.log(err);
                            result(err, null);
                            return;
                        }
                        result(null, datos);
                    });
                });
            });
        });
    });
}

Usuario.eliUsuarioRepartidor = (datos, result) => {
    // 1. Eliminar pedidos del repartidor
    let sql = 'DELETE FROM pedidos WHERE idRepartidor=?';
    db.query(sql, [datos.idUsuario], (err, res) => {
        if (err) {
            console.log(err);
            result(err, null);
            return;
        }
        // 2. Eliminar de repartidores
        sql = 'DELETE FROM repartidores WHERE idUsuario=?';
        db.query(sql, [datos.idUsuario], (err, res) => {
            if (err) {
                console.log(err);
                result(err, null);
                return;
            }
            // 3. Buscar información del usuario para obtener idPersona
            sql = 'SELECT * FROM usuarios WHERE id=?';
            db.query(sql, [datos.idUsuario], (err, res) => {
                if (err) {
                    console.log(err);
                    result(err, null);
                    return;
                }
                var usuario = res[0];
                // 4. Primero, eliminar el usuario
                sql = 'DELETE FROM usuarios WHERE id=?';
                db.query(sql, [datos.idUsuario], (err, res) => {
                    if (err) {
                        console.log(err);
                        result(err, null);
                        return;
                    }
                    // 5. Luego, eliminar de personas
                    sql = 'DELETE FROM personas WHERE id=?';
                    db.query(sql, [usuario.idPersona], (err, res) => {
                        if (err) {
                            console.log(err);
                            result(err, null);
                            return;
                        }
                        result(null, datos);
                    });
                });
            });
        });
    });
}


Usuario.registro = async (usuario, result) => {

    const hash = await bcrypt.hash(usuario.clave, 10);

    var sql = 'INSERT INTO personas(nombres, apellidos, cedula, telefono, direccion, email, estado) VALUES(?, ?, ?, ?, ?, ?, ?)'
    db.query(
        sql,
        [
            usuario.nombres,
            usuario.apellidos,
            usuario.cedula,
            usuario.telefono,
            usuario.direccion,
            usuario.email,
            usuario.estado
        ],
        (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            } else {
                usuario.idPersona = res.insertId
                if (usuario.rol == "Administrador") {
                    usuario.idRol = 1
                } else if (usuario.rol == "Empleado") {
                    usuario.idRol = 2
                } else if (usuario.rol == "Cliente") {
                    usuario.idRol = 3
                } else if (usuario.rol == "Repartidor") {
                    usuario.idRol = 4
                }
                sql = 'INSERT INTO usuarios(idPersona, idRol, usuario, clave, estado) VALUES(?, ?, ?, ?, ?)'
                db.query(
                    sql,
                    [
                        usuario.idPersona,
                        usuario.idRol,
                        usuario.usuario,
                        hash,
                        'ACTIVO'
                    ],
                    (err, res) => {
                        if (err) {
                            console.log(err)
                            result(err, null)
                        } else {
                            usuario.idUsuario = res.insertId
                            if (usuario.rol == "Cliente") {
                                sql = 'INSERT INTO clientes(idUsuario, estado) VALUES(?, ?)'
                                db.query(
                                    sql,
                                    [
                                        usuario.idUsuario,
                                        'ACTIVO'
                                    ],
                                    (err, res) => {
                                        if (err) {
                                            console.log(err)
                                            result(err, null)
                                        } else {
                                            result(null, res.insertId)
                                        }
                                    }
                                )
                            } else if (usuario.rol == "Repartidor") {
                                sql = 'INSERT INTO repartidores(idUsuario, estado, lat, lng) VALUES(?,?,?,?)'
                                db.query(
                                    sql,
                                    [
                                        usuario.idUsuario,
                                        'ACTIVO',
                                        0.0,
                                        0.0
                                    ],
                                    (err, res) => {
                                        if (err) {
                                            console.log(err)
                                            result(err, null)
                                        } else {
                                            result(null, res.insertId)
                                        }
                                    }
                                )
                            } else {
                                result(null, res.insertId)
                            }
                        }
                    }
                )
            }
        }
    )
}

module.exports = Usuario;