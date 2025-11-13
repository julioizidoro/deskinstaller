/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loja")
public class Loja implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loja")
    private List<Vendedor> vendedorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loja")
    private List<Apcliente> apclienteList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idloja")
    private Integer idloja;
    @Basic(optional = false)
    @Column(name = "nome")
    private String nome;
    @Column(name = "foneComercial")
    private String foneComercial;
    @Column(name = "foneFax")
    private String foneFax;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorComissao")
    private Double valorComissao;


    
}
