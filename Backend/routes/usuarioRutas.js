const usuarioControl = require('../controllers/usuarioControl');

module.exports = (app) => {
    app.post('/api/usuarios/registro', usuarioControl.registro)
    app.post('/api/login', usuarioControl.login)
    app.post('/api/usuarios/tomarRol', usuarioControl.tomarRol)
    app.post('/api/usuarios/actPerfil', usuarioControl.actPerfil)
    app.post('/api/usuarios/actPass', usuarioControl.actPass)
    app.post('/api/usuarios/actUsuario', usuarioControl.actUsuario)
    app.post('/api/usuarios/eliUsuario', usuarioControl.eliUsuario)
    app.post('/api/usuarios/eliUsuarioRepartidor', usuarioControl.eliUsuarioRepartidor)

    app.get('/api/usuarios/listaClientes', usuarioControl.listaClientes)
    app.get('/api/usuarios/listaRepartidores', usuarioControl.listaRepartidores)
    app.get('/api/usuarios/listaUsuarios', usuarioControl.listaUsuarios)
}