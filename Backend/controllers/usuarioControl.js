
const Usuario = require('../models/usuarioModel');
const bcrypt = require('bcryptjs');
const Jwt = require('jsonwebtoken');
const keys = require('../config/keys');

module.exports = {

    login(req, res) {
        
        const usuario = req.body.usuario;
        const clave = req.body.clave;        

        Usuario.FindByUsername(usuario, async (err, data) => {

            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }

            if (!data) {
                return res.status(401).json({
                    success: false,
                    mensaje: "El Usuario no fue encontrado",
                })
            }

            const isPasswordValid = await bcrypt.compare(clave, data.clave)

            if (isPasswordValid) {
                const token = Jwt.sign({
                    id: data.id,
                    usuario: data.usuario
                }, keys.secretOrKey, {

                })

                const dataJwt = {
                    idUsuario: data.idUsuario,
                    usuario: data.usuario,
                    estado: data.estado,
                    nombres: data.nombres,
                    apellidos: data.apellidos,
                    cedula: data.cedula,
                    telefono: data.telefono,
                    direccion: data.direccion,
                    email: data.email,
                    rol: data.rol,
                    idPersona: data.idPersona,
                    idRol: data.idRol,
                    session_token: 'JWT ' + token
                    //todo lo que vaya a devolver
                }
                return res.status(201).json({
                    success: true,
                    mensaje: "El Usuario fue autenticado con exito",
                    data: dataJwt
                })
            } else {
                return res.status(401).json({
                    success: false,
                    mensaje: "El Usuario o la contraseña son incorrectas"
                })
            }
        })

    },

    registro(req, res) {
        const usuario = req.body;
        Usuario.registro(usuario, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "El Cliente ha sido registrado exitosamente",
                data: data
            })
        })
    },
    tomarRol(req, res) {
        const id = req.body.id;
        const rol = req.body.rol;
        const obj={id:id,rol:rol}
        Usuario.tomarRol(obj, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "se ha tomado el rol exitosamente",
                data: data
            })
        })
    },
    actPerfil(req,res){
        const usuario = req.body;
        Usuario.actPerfil(usuario, (err, data) => {
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
    actPass(req,res){
        const usuario = req.body;
        Usuario.actPass(usuario, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "La contraseña no conincide con la guardada en la base de datos",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Se actualizo la contraseña correctamente",
                data: data
            })
        })
    },
    listaUsuarios(req,res){
        Usuario.listaUsuarios(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "ha existido un error",
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
    listaClientes(req,res){
        Usuario.listaClientes(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "ha existido un error",
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
    listaRepartidores(req,res){
        Usuario.listaRepartidores(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "ha existido un error",
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
    actUsuario(req,res){
        const usuario = req.body;
        Usuario.actUsuario(usuario, (err, data) => {
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
    eliUsuario(req,res){
        const usuario = req.body;
        Usuario.eliUsuario(usuario, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Usuario eliminado correctamente",
                data: data
            })
        })
    },
    eliUsuarioRepartidor(req,res){
        const usuario = req.body;
        Usuario.eliUsuarioRepartidor(usuario, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Usuario eliminado correctamente",
                data: data
            })
        })
    }
}
