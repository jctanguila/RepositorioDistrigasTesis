module.exports = (io) => {

    const namespace = io.of('/pedidos/delivery');
    namespace.on('connection', (socket) => {

        console.log('USUARIO SE CONECTO A SOCKET IO: /pedidos/delivery');

        socket.on('pedidoAsignado', (data) => {
            console.log('ADMINISTRADOR: ', data);
            namespace.emit(`notiPedidoAsignado`, { id_Pedido: data.idPedido, id_Cliente: data.idCliente, lat_Cliente: data.latCliente, lng_Cliente: data.lngCliente });
            namespace.emit(`nuevo_Pedido/${data.idPedido}`, { id_Cliente: data.idCliente, lat_Cliente: data.latCliente, lng_Cliente: data.lngCliente });
        });
        socket.on('pedidosGenerados', (data) => {
            console.log('CLIENTE EMITIO: ', data);
            namespace.emit(`notiPedidoGenerado`, { id_cliente: data.id, lat: data.lat, lng: data.lng  });            
        });

        socket.on('pedidoFinalizado', (data) => {
            console.log('CLIENTE EMITIO: ', data);
            namespace.emit(`notiPedidoLlego/${data.idCliente}`, { idPedido: data.idPedido }); 
            namespace.emit(`notiPedidoFinalizo/${data.idCliente}`, { idPedido: data.idPedido });            
        });
        
        socket.on('ubicacionRepartidor', (data) => {
            console.log('REPARTIDOR EMITIO UBICACION: ', data);
            namespace.emit(`repartidor`, { idRepartidor: data.id, latRepartidor: data.lat, lngRepartidor: data.lng  });            
        });

        socket.on('position', (data) => {
            console.log('REPARTIDOR EMITIO: ', data);
            namespace.emit(`position/${data.id}`, { id_pedido: data.id, lat: data.lat, lng: data.lng  });            
        });

        socket.on('disconnect', (data) => {
            console.log('UN USUARIO SE DESCONECTO DE SOCKET IO');
        });

    });

}