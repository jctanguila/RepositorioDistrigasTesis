const Pedido = require('../models/pedidosModel'); // Importar el modelo de Pedidos
const Cliente = require('../models/clienteModel'); // Importar el modelo de Clientes
const Repartidor = require('../models/repartidorModel'); // Importar el modelo de Repartidores


module.exports = {
    // Metodo para obtener los pedidos pendientes
    pedidosPendientes(req,res){
        Pedido.pedidosPendientes(req.body,(err, data) => { // Llamar al metodo "pedidosPendientes" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver los datos de los pedidos pendientes
                success: true,
                success: true,
                data: data
            })
        })
    },
    // Metodo para obtener los pedidos asignados
    pedidosAsignados(req,res){
        Pedido.pedidosAsignados(req.body,(err, data) => { // Llamar al metodo "pedidosAsignados" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver los datos de los pedidos asignados
                success: true,
                data: data
            })
        })
    },
    // Metodo para asignar un repartidor a un pedido
    asignarRepartidor(req,res){
        const pedido = req.body;
        Pedido.asignarRepartidor(pedido, (err, data) => { // Llamar al metodo "asignarRepartidor" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de exito y los datos del pedido actualizado
                success: true,
                mensaje: "Actualizacion realizada correctamente",
                data: data
            })
        })
    },
    // Metodo para crear un nuevo pedido
    crearPedido(req, res){
        var pedido = req.body;
        Cliente.findByUsuario(pedido.idCliente, (err, data) => { // Buscar al cliente por su usuario
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            pedido.idCliente = data.id // Asignar el ID del cliente al pedido
            Pedido.crearPedido(pedido, (err, data) => { // Llamar al metodo "crearPedido" del modelo de Pedidos
                if (err) {
                    return res.status(501).json({ // Devolver un error si hay un error en la consulta
                        success: false,
                        mensaje: "Hubo un error",
                        error: err
                    })
                }
                return res.status(201).json({ // Devolver un mensaje de exito y los datos del pedido creado
                    success: true,
                    success: true,
                    mensaje: "El Pedido ha sido registrado exitosamente",
                    data: data
                })
            })
        })        
    },
    // Metodo para obtener los pedidos de un cliente
    pedidosCliente(req, res){
        var pedido = req.body;
        Cliente.findByUsuario(pedido.idCliente, (err, data) => { // Buscar al cliente por su usuario
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            } 
            if (!data) {
                return res.status(401).json({ // Devolver un error si el usuario no es de tipo cliente
                    success: false,
                    mensaje: "El Usuario no es de tipo Cliente",
                })
            }          
            Pedido.pedidosCliente(data.id,(err, data) => { // Llamar al metodo "pedidosCliente" del modelo de Pedidos
                if (err) {
                    return res.status(501).json({ // Devolver un error si hay un error en la consulta
                        success: false,
                        mensaje: "Hubo un error",
                        error: err
                    })
                }
                if (!data) {
                    return res.status(401).json({ // Devolver un error si no hay historial de pedidos
                        success: false,
                        mensaje: "No existe un historial de pedidos",
                    })
                } 
                return res.status(201).json({ // Devolver los datos de los pedidos del cliente
                    success: true,
                    data: data
                })
            })
        }) 
    },
    // Metodo para obtener los pedidos de un repartidor
    pedidosRepartidor(req, res){
        var pedido = req.body;        
        Repartidor.findByUsuario(pedido.idUsuario, (err, data) => { // Buscar al repartidor por su usuario
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false, 
                    mensaje: "Hubo un error",
                    error: err
                })
            } 
            if (!data) {
                return res.status(401).json({ // Devolver un error si el usuario no es de tipo repartidor
                    success: false, 
                    mensaje: "El Usuario no es de tipo Repartidor",
                })
            }          
            Pedido.pedidosRepartidor(data.id,(err, data) => { // Llamar al metodo "pedidosRepartidor" del modelo de Pedidos
                if (err) {
                    return res.status(501).json({ // Devolver un error si hay un error en la consulta
                        success: false,
                        mensaje: "Hubo un error",
                        error: err
                    })
                }
                if (!data) {
                    return res.status(401).json({ // Devolver un error si no hay pedidos asignados al repartidor
                        success: false,
                        mensaje: "No existe pedidos asignados al Repartidor",
                    })
                } 
                return res.status(201).json({ // Devolver los datos de los pedidos del repartidor
                    success: true,
                    data: data
                })
            })
        }) 
    },
    // Metodo para obtener las ubicaciones de un pedido
    buscarUbicacionesPedido(req,res){
        const idPedido = req.body.idPedido;
        Pedido.ubicacionesPedido(idPedido, (err, data) => { // Llamar al metodo "ubicacionesPedido" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de exito y los datos de las ubicaciones del pedido
                success: true,
                mensaje: "Ubicaciones encontradas exitosamente",
                data: data
            })
        })
    },
    // Metodo para obtener los pedidos finalizados
    pedidosFinalizados(req,res){
        Pedido.pedidosFinalizados(req.body,(err, data) => { // Llamar al metodo "ubicacionesPedido" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de exito y los datos de las ubicaciones del pedido
                success: true,
                data: data
            })
        })
    },
    // Método para finalizar un pedido
    finalizarPedido(req,res){
        Pedido.finalizarPedido(req.body,(err, data) => { // Llamar al metodo "finalizarPedido" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de éxito y los datos del pedido finalizado
                mensaje: "Pedido finalizado exitosamente",
                data: data
            })
        })
    },// Metodo para obtener las ubicaciones de un repartidor
    buscarUbicacionesRepartidor(req,res){
        const idRepartidor = req.body.idRepartidor;
        Repartidor.findById(idRepartidor, (err, data) => { // Buscar al repartidor por su ID
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de éxito y los datos de las ubicaciones del repartidor
                success: true,
                mensaje: "Ubicaciones encontradas exitosamente",
                data: data
            })
        })
    },// Metodo para agregar un nuevo comentario a un pedido
    nuevoComentario(req,res){        
        Pedido.nuevoComentario(req.body, (err, data) => { // Llamar al metodo "nuevoComentario" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de exito
                success: true,
                mensaje: "Comentario agregado exitosamente"
            })
        })
    },
    // Metodo para buscar repartidores
    buscarRepartidores(req,res){
        Repartidor.findAll(req.body, (err, data) => { // Buscar al repartidor por su ID
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de exito y los datos de las ubicaciones del repartidor
                success: true, 
                mensaje: "",
                data: data
            })
        })
    },
    // Metodo para asignar repartidor
    asignarRepartidorAutomatico(req,res){
        Pedido.asignarRepartidorAutomatico(req.body, (err, data) => { // Llamar al metodo "asignarRepartidorAutomatico" del modelo de Pedidos
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de exito y los datos de la asignacion del repartidor
                success: true,
                mensaje: "Repartidor asignado con éxito"
            })
        })
    },
    // Metodo para actualizar la ubicación de un repartidor
    actualizarUbicacionRepartidor(req,res){
        Repartidor.actUbicacion(req.body, (err, data) => { // Llamar al metodo "actUbicacion" del modelo de Repartidores
            if (err) {
                return res.status(501).json({ // Devolver un error si hay un error en la consulta
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devolver un mensaje de éxito
                success: true,
                mensaje: "Ubicacion almacenada en la BD"
            })
        })
    }
}