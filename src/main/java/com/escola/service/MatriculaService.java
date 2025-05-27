package com.escola.service;

import com.escola.dto.MatriculaDTO;
import com.escola.dto.MatriculaRequestDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.model.Matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaService {

    MatriculaDTO realizarMatricula(MatriculaRequestDTO matriculaRequestDTO)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

    List<Matricula> listarTodasMatriculasComNomes();

    Optional<Matricula> buscarPorId(Long id);

    MatriculaDTO atualizar(Matricula matriculaInput)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

    void remover(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

    List<MatriculaDTO> listarTodasMatriculasComDetalhes();

    MatriculaDTO buscarMatriculaPorIdComDetalhes(Long id) throws EntidadeNaoEncontradaException;

    void cancelarMatricula(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

}
