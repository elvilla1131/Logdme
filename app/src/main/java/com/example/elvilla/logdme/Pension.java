package com.example.elvilla.logdme;


import java.util.Date;

public class Pension {
    public String id_pension, id_usuario, url_imagen, titulo, descripcion, barrio, restricciones,no_huespedes, serv_lavadora, precio, thumbnail;
    public Date timestamp;

    public Pension (){}

    public Pension(String id_pension, String id_usuario, String url_imagen, String titulo, String descripcion, String barrio, String restricciones, String no_huespedes, String serv_lavadora, String precio, String thumbnail, Date timestamp) {
        this.id_pension = id_pension;
        this.id_usuario = id_usuario;
        this.url_imagen = url_imagen;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.barrio = barrio;
        this.restricciones = restricciones;
        this.no_huespedes = no_huespedes;
        this.serv_lavadora = serv_lavadora;
        this.precio = precio;
        this.thumbnail = thumbnail;
        this.timestamp = timestamp;
    }

    public String getId_pension() {
        return id_pension;
    }

    public void setId_pension(String id_pension) {
        this.id_pension = id_pension;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getRestricciones() {
        return restricciones;
    }

    public void setRestricciones(String restricciones) {
        this.restricciones = restricciones;
    }

    public String getNo_huespedes() {
        return no_huespedes;
    }

    public void setNo_huespedes(String no_huespedes) {
        this.no_huespedes = no_huespedes;
    }

    public String getServ_lavadora() {
        return serv_lavadora;
    }

    public void setServ_lavadora(String serv_lavadora) {
        this.serv_lavadora = serv_lavadora;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}

