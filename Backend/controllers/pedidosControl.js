const Pedido = require('../models/pedidosModel');
const Cliente = require('../models/clienteModel');
const Repartidor = require('../models/repartidorModel');

module.exports = {
    pedidosPendientes(req,res){
        Pedido.pedidosPendientes(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                data: data
            })
        })
    },
    pedidosAsignados(req,res){
        Pedido.pedidosAsignados(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                data: data
            })
        })
    },
    asignarRepartidor(req,res){
        const pedido = req.body;
        Pedido.asignarRepartidor(pedido, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Actualizacion realizada correctamente",
                data: data
            })
        })
    },
    crearPedido(req, res){
        var pedido = req.body;
        Cliente.findByUsuario(pedido.idCliente, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            pedido.idCliente = data.id
            Pedido.crearPedido(pedido, (err, data) => {
                if (err) {
                    return res.status(501).json({
                        success: false,
                        mensaje: "Hubo un error",
                        error: err
                    })
                }
                return res.status(201).json({
                    success: true,
                    mensaje: "El Pedido ha sido registrado exitosamente",
                    data: data
                })
            })
        })        
    },
    pedidosCliente(req, res){
        var pedido = req.body;
        Cliente.findByUsuario(pedido.idCliente, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            } 
            if (!data) {
                return res.status(401).json({
                    success: false,
                    mensaje: "El Usuario no es de tipo Cliente",
                })
            }          
            Pedido.pedidosCliente(data.id,(err, data) => {
                if (err) {
                    return res.status(501).json({
                        success: false,
                        mensaje: "Hubo un error",
                        error: err
                    })
                }
                if (!data) {
                    return res.status(401).json({
                        success: false,
                        mensaje: "No existe un historial de pedidos",
                    })
                } 
                return res.status(201).json({
                    success: true,
                    data: data
                })
            })
        }) 
    },
    pedidosRepartidor(req, res){
        var pedido = req.body;        
        Repartidor.findByUsuario(pedido.idUsuario, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            } 
            if (!data) {
                return res.status(401).json({
                    success: false,
                    mensaje: "El Usuario no es de tipo Repartidor",
                })
            }          
            Pedido.pedidosRepartidor(data.id,(err, data) => {
                if (err) {
                    return res.status(501).json({
                        success: false,
                        mensaje: "Hubo un error",
                        error: err
                    })
                }
                if (!data) {
                    return res.status(401).json({
                        success: false,
                        mensaje: "No existe pedidos asignados al Repartidor",
                    })
                } 
                return res.status(201).json({
                    success: true,
                    data: data
                })
            })
        }) 
    },
    buscarUbicacionesPedido(req,res){
        const idPedido = req.body.idPedido;
        Pedido.ubicacionesPedido(idPedido, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Ubicaciones encontradas exitosamente",
                data: data
            })
        })
    },
    pedidosFinalizados(req,res){
        Pedido.pedidosFinalizados(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                data: data
            })
        })
    },
    finalizarPedido(req,res){
        Pedido.finalizarPedido(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Pedido finalizado exitosamente",
                data: data
            })
        })
    },
    buscarUbicacionesRepartidor(req,res){
        const idRepartidor = req.body.idRepartidor;
        Repartidor.findById(idRepartidor, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Ubicaciones encontradas exitosamente",
                data: data
            })
        })
    },
    nuevoComentario(req,res){        
        Pedido.nuevoComentario(req.body, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Comentario agregado exitosamente"
            })
        })
    },
    buscarRepartidores(req,res){
        Repartidor.findAll(req.body, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "",
                data: data
            })
        })
    },
    asignarRepartidorAutomatico(req,res){
        Pedido.asignarRepartidorAutomatico(req.body, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Repartidor asignado con éxito"
            })
        })
    },
    actualizarUbicacionRepartidor(req,res){
        Repartidor.actUbicacion(req.body, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Ubicacion almacenada en la BD"
            })
        })
    }
}