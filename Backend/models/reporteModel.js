const db = require('../config/config');

const Reporte = {};

Reporte.reporteInventario = (req, result) => {
	const sql = `SELECT
	productos.id,
	COUNT(pedidos.id)as numero,
	productos.producto, 
	productos.costo, 
	productos.pvp, 
	SUM(pedidosds.cantidad)as cantidad,
	(productos.costo*SUM(pedidosds.cantidad))as invertido,
	(productos.pvp*SUM(pedidosds.cantidad))as recuperado,
	((productos.pvp*SUM(pedidosds.cantidad))-(productos.costo*SUM(pedidosds.cantidad)))as ganancia
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
	GROUP BY pedidosds.idProducto`
	db.query(sql, (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Reporte.reporteInventarioFecha = async (ventas, result) => {
    var sql = `SELECT
	productos.id,
	COUNT(pedidos.id)as numero,
	productos.producto, 
	productos.costo, 
	productos.pvp, 
	SUM(pedidosds.cantidad)as cantidad,
	(productos.costo*SUM(pedidosds.cantidad))as invertido,
	(productos.pvp*SUM(pedidosds.cantidad))as recuperado,
	((productos.pvp*SUM(pedidosds.cantidad))-(productos.costo*SUM(pedidosds.cantidad)))as ganancia
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
		WHERE pedidos.estado='FINALIZADO' AND (pedidos.fecha>=? AND pedidos.fecha<=?)
	GROUP BY pedidosds.idProducto`
    db.query(
        sql,
        [
           ventas.fechaIni,
		   ventas.fechaFin
        ],
        (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            } else {
				//console.log(res)
                result(null, res)
            }
        }
    )
}

Reporte.reporteClientesFecha = async (ventas, result) => {
    var sql = `SELECT
    pedidos.id,
	personas.cedula, 
	personas.apellidos, 
	personas.nombres, 
    DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha,
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total,
	COUNT(pedidos.idCliente)as numero
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
	WHERE pedidos.estado='FINALIZADO' AND (pedidos.fecha>=? AND pedidos.fecha<=?)
	GROUP BY pedidos.idCliente`
    db.query(
        sql,
        [
           ventas.fechaIni,
		   ventas.fechaFin
        ],
        (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            } else {
				console.log(res)
                result(null, res)
            }
        }
    )
}

Reporte.reporteCliente = (req, result) => {
	const sql = `SELECT
    pedidos.id,
	personas.cedula, 
	personas.apellidos, 
	personas.nombres, 
    DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha,
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total,
	COUNT(pedidos.idCliente)as numero
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
	WHERE pedidos.estado='FINALIZADO'
	GROUP BY pedidos.idCliente`
	db.query(sql, (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

Reporte.reporteVentasFecha = async (ventas, result) => {
	console.log(ventas)
    var sql = `SELECT
    pedidos.id,
	personas.cedula, 
	personas.apellidos, 
	personas.nombres, 
    DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha,
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total
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
	WHERE pedidos.estado='FINALIZADO' AND (pedidos.fecha>=? AND pedidos.fecha<=?)`
    db.query(
        sql,
        [
           ventas.fechaIni,
		   ventas.fechaFin
        ],
        (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            } else {
				console.log(res)
                result(null, res)
            }
        }
    )
}

Reporte.reporteVentas = (req, result) => {
	const sql = `SELECT
    pedidos.id,
	personas.cedula, 
	personas.apellidos, 
	personas.nombres, 
    DATE_FORMAT(pedidos.fecha, "%d/%m/%Y") as fecha,
	pedidos.hora, 
	pedidos.subtotal, 
	pedidos.iva, 
	pedidos.total
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
	WHERE pedidos.estado='FINALIZADO'`
	db.query(sql, (err, res) => {
		if (err) {
			result(err, null)
		} else {
			result(null, res)
		}
	})
}

module.exports = Reporte;
