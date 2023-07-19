const pedidosControl = require('../controllers/pedidosControl');

module.exports = (app) => {

    app.post('/api/pedidos/asignarRepartidor', pedidosControl.asignarRepartidor)    
    app.get('/api/pedidos/pedidosAsignados', pedidosControl.pedidosAsignados)
    app.get('/api/pedidos/pedidosPendientes', pedidosControl.pedidosPendientes)
    app.post('/api/pedidos/crearPedido', pedidosControl.crearPedido)
    app.post('/api/pedidos/pedidosCliente', pedidosControl.pedidosCliente)
    app.post('/api/pedidos/pedidosRepartidor', pedidosControl.pedidosRepartidor)
    app.post('/api/pedidos/buscarUbicacionesPedido', pedidosControl.buscarUbicacionesPedido)
    app.get('/api/pedidos/pedidosFinalizados', pedidosControl.pedidosFinalizados)
    app.post('/api/pedidos/finalizarPedido', pedidosControl.finalizarPedido)
    app.post('/api/pedidos/buscarUbicacionesRepartidor', pedidosControl.buscarUbicacionesRepartidor)
    app.post('/api/pedidos/nuevoComentario', pedidosControl.nuevoComentario)
    app.post('/api/pedidos/buscarRepartidores', pedidosControl.buscarRepartidores)
    app.post('/api/pedidos/asignarRepartidorAutomatico', pedidosControl.asignarRepartidorAutomatico) 
    app.post('/api/pedidos/actualizarUbicacionRepartidor', pedidosControl.actualizarUbicacionRepartidor) 
    
}