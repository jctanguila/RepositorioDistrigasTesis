const db = require('../config/config');

const Pedido = {};

Pedido.pedidosPendientes = (req, result) => {
	const sql = `SELECT
	personas.nombres, 
	personas.apellidos, 
	personas.cedula, 
	personas.telefono, 
	personas.direccion, 
	personas.email,
	pedidos.id, 
	DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha,
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total, 
	clientes.lat, 
	clientes.lng
FROM
	pedidos
	INNER JOIN
	clientes
	ON 
		pedidos.idCliente = clientes.id
	INNER JOIN
	usuarios
	ON 
		clientes.idUsuario = usuarios.id
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
		WHERE pedidos.estado='GENERADO'`
	db.query(sql, (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Pedido.pedidosAsignados = (req, result) => {
	const sql = `SELECT
	personas.nombres AS clienteNombres, 
	personas.apellidos AS clienteApellidos, 
	personas.cedula AS clienteCedula, 
	personas.telefono AS clienteTelefono, 
	personas.direccion AS clienteDireccion, 
	personas.email AS clienteEmail, 
	clientes.lng AS clienteLng, 
	clientes.lat AS clienteLat, 
	pedidos.id, 
	DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") AS fecha, 
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total, 
	repartidores.lat AS repartidorLat, 
	repartidores.lng AS repartidorLng, 
	CONCAT(personas2.nombres,' ',personas2.apellidos) AS repartidorNombres, 
	personas2.telefono AS repartidorTelefono, 
	personas2.direccion AS repartidorDireccion, 
	personas2.cedula AS repartidorCedula, 
	personas2.email AS repartidorEmail, 
	personas2.nombres AS repartidorNombre, 
	personas2.apellidos AS repartidorApellido, 
	productos.producto, 
	pedidosds.cantidad, 
	pedidos.estado
FROM
	pedidos
	INNER JOIN
	clientes
	ON 
		pedidos.idCliente = clientes.id
	INNER JOIN
	usuarios
	ON 
		clientes.idUsuario = usuarios.id
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
	INNER JOIN
	repartidores
	ON 
		pedidos.idRepartidor = repartidores.id
	INNER JOIN
	usuarios AS usuarios2
	ON 
		repartidores.idUsuario = usuarios2.id
	INNER JOIN
	personas AS personas2
	ON 
		usuarios2.idPersona = personas2.id
	INNER JOIN
	pedidosds
	ON 
		pedidos.id = pedidosds.idPedido
	INNER JOIN
	productos
	ON 
		pedidosds.idProducto = productos.id
WHERE
	pedidos.estado = 'ASIGNADO'`
	db.query(sql, (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Pedido.asignarRepartidor = (datos, result) => {
	var sql = 'UPDATE pedidos SET idRepartidor=?, estado="ASIGNADO" WHERE id=?'
	db.query(
		sql,
		[
			datos.idRepartidor,
			datos.idPedido
		], (err, res) => {
			if (err) {
				console.log(err)
				result(err, null)
			} else {
				result(null, datos)
			}
	})
}

Pedido.crearPedido = async (pedido, result) => {	
	var sql = 'INSERT INTO pedidos(idCliente, fecha, hora, subtotal, iva, total, estado) VALUES(?, ?, ?, ?, ?, ?, ?)'
	db.query(
		sql,
		[
			pedido.idCliente,
			pedido.fecha,
			pedido.hora,
			pedido.subtotal,
			pedido.iva,
			pedido.total,
			'GENERADO'
		],
		(err, res) => {
			if (err) {
				console.log(err)
				result(err, null)
			} else {
				var idPedido = res.insertId
				var sql = 'INSERT INTO pedidosds(idPedido, idProducto, cantidad) VALUES(?, ?, ?)'
				db.query(
					sql,
					[
						idPedido,
						pedido.idProducto,
						pedido.cantidad
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
			}
		}
	)
}

Pedido.pedidosCliente = (idCliente, result) => {
	const sql = `SELECT
	pedidos.id, 
	pedidos.idCliente, 
	pedidos.idRepartidor, 
	DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha, 
	pedidos.hora, 
	pedidos.total, 
	pedidos.estado, 
	pedidos.comentario,
	productos.producto, 
	productos.descripcion, 
	pedidosds.cantidad, 
	productos.imagen
	FROM
	pedidos
	INNER JOIN
	pedidosds
	ON 
	pedidos.id = pedidosds.idPedido
	INNER JOIN
	productos
	ON 
	pedidosds.idProducto = productos.id
	WHERE pedidos.idCliente=?
	ORDER BY fecha DESC`
	db.query(sql, [idCliente], (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Pedido.pedidosRepartidor = (idRepartidor, result) => {
	const sql = `SELECT
	pedidos.id, 
	pedidos.idCliente, 
	pedidos.idRepartidor, 
	DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha, 
	pedidos.hora, 
	pedidos.total, 
	pedidos.estado, 
	productos.producto, 
	productos.descripcion, 
	pedidosds.cantidad, 
	productos.imagen
	FROM
	pedidos
	INNER JOIN
	pedidosds
	ON 
	pedidos.id = pedidosds.idPedido
	INNER JOIN
	productos
	ON 
	pedidosds.idProducto = productos.id
	WHERE pedidos.idRepartidor=?
	ORDER BY fecha DESC`
	db.query(sql, [idRepartidor], (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Pedido.ubicacionesPedido = (idPedido, result) => {
	const sql = `SELECT
	clientes.lat, 
	clientes.lng,
	clientes.idUsuario
	FROM
	pedidos
	INNER JOIN
	clientes
	ON 
	pedidos.idCliente = clientes.id
	WHERE pedidos.id=?`
	db.query(sql, [idPedido], (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res[0])
		}
	})
}

Pedido.pedidosFinalizados = (req, result) => {
	const sql = `SELECT
	personas.nombres AS clienteNombres, 
	personas.apellidos AS clienteApellidos, 
	personas.cedula AS clienteCedula, 
	personas.telefono AS clienteTelefono, 
	personas.direccion AS clienteDireccion, 
	personas.email AS clienteEmail, 
	clientes.lng AS clienteLng, 
	clientes.lat AS clienteLat, 
	pedidos.id, 
	DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") AS fecha, 
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total, 
	repartidores.lat AS repartidorLat, 
	repartidores.lng AS repartidorLng, 
	CONCAT(personas2.nombres,' ',personas2.apellidos) AS repartidorNombres, 
	personas2.telefono AS repartidorTelefono, 
	personas2.direccion AS repartidorDireccion, 
	personas2.cedula AS repartidorCedula, 
	personas2.email AS repartidorEmail, 
	personas2.nombres AS repartidorNombre, 
	personas2.apellidos AS repartidorApellido, 
	productos.producto, 
	pedidosds.cantidad, 
	pedidos.estado
FROM
	pedidos
	INNER JOIN
	clientes
	ON 
		pedidos.idCliente = clientes.id
	INNER JOIN
	usuarios
	ON 
		clientes.idUsuario = usuarios.id
	INNER JOIN
	personas
	ON 
		usuarios.idPersona = personas.id
	INNER JOIN
	repartidores
	ON 
		pedidos.idRepartidor = repartidores.id
	INNER JOIN
	usuarios AS usuarios2
	ON 
		repartidores.idUsuario = usuarios2.id
	INNER JOIN
	personas AS personas2
	ON 
		usuarios2.idPersona = personas2.id
	INNER JOIN
	pedidosds
	ON 
		pedidos.id = pedidosds.idPedido
	INNER JOIN
	productos
	ON 
		pedidosds.idProducto = productos.id
WHERE
	pedidos.estado = 'FINALIZADO'`
	db.query(sql, (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Pedido.finalizarPedido = (datos, result) => {
	var sql = 'UPDATE pedidos SET estado="FINALIZADO" WHERE id=?'
	db.query(
		sql,
		[
			datos.idPedido
		], (err, res) => {
			if (err) {
				console.log(err)
				result(err, null)
			} else {
				result(null, datos)
			}
	})
}

Pedido.nuevoComentario = (datos, result) => {
	var sql = 'UPDATE pedidos SET comentario=? WHERE id=?'
	db.query(
		sql,
		[
			datos.comentario,
			datos.idPedido
		], (err, res) => {
			if (err) {
				console.log(err)
				result(err, null)
			} else {
				result(null, datos)
			}
	})
}

Pedido.asignarRepartidorAutomatico = (datos, result) => {
	var sql = 'UPDATE pedidos SET idRepartidor=?, estado="ASIGNADO", origenLat=?, origenLng=?, destinoLat=?, destinoLng=? WHERE id=?'
	db.query(
		sql,
		[
			datos.idRepartidor,
			datos.origenLat,
			datos.origenLng,
			datos.destinoLat,
			datos.destinoLng,
			datos.idPedido
		], (err, res) => {
			if (err) {
				console.log(err)
				result(err, null)
			} else {
				result(null, datos)
			}
	})
}

module.exports = Pedido;