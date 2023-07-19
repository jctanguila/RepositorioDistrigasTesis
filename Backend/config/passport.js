const JwtStrategy = require('passport-jwt').Strategy;
const ExtractJwt = require('passport-jwt').ExtractJwt;
const keys = require('./keys');

const Usuario = require('../models/usuarioModel')

module.exports = (passport)=>{
    let opts ={}
    opts.jwtFromRequest = ExtractJwt.fromAuthHeaderWithScheme("jwt");
    opts.secretOrKey= keys.secretOrKey;

    passport.use(new JwtStrategy(opts,(jwt_payload, done)=>{

        Usuario.FindById(jwt_payload.id,(err,usuario)=>{

            if (err){
                return done(err,false)
            }
            if (usuario){
                return done(null, usuario)
            }else{
               return done(null,false) 
            }
        })

    }))
}