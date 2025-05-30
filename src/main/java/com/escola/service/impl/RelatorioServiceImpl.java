package com.escola.service.impl;

import com.escola.dto.RelatorioCursoDTO;
import com.escola.model.Curso;
import com.escola.model.Matricula;
import com.escola.repository.CursoRepository;
import com.escola.repository.MatriculaRepository;
import com.escola.repository.impl.CursoRepositoryImpl;
import com.escola.repository.impl.MatriculaRepositoryImpl;
import com.escola.service.RelatorioService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class RelatorioServiceImpl implements RelatorioService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;

    public RelatorioServiceImpl(MatriculaRepository matriculaRepository, CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
    }

    public RelatorioServiceImpl() {
        this.matriculaRepository = new MatriculaRepositoryImpl();
        this.cursoRepository = new CursoRepositoryImpl();
    }

    public RelatorioServiceImpl(MatriculaRepositoryImpl matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = new CursoRepositoryImpl();
    }

    @Override
    public List<RelatorioCursoDTO> gerarRelatorioEngajamentoCursos() {
        List<RelatorioCursoDTO> relatorios = new ArrayList<>();
        List<Curso> cursos = cursoRepository.listarTodos();

        for (Curso curso : cursos) {
            List<Matricula> matriculas = matriculaRepository.listarPorCursoId(curso.getId());
            long totalMatriculados = matriculas.size();
            double mediaIdade = calcularMediaIdade(matriculas);
            long novosAlunos = contarNovosAlunosNosUltimosDias(matriculas, 30);

            RelatorioCursoDTO relatorio = new RelatorioCursoDTO(curso.getNome(), totalMatriculados, mediaIdade, novosAlunos);
            relatorios.add(relatorio);
        }
        return relatorios;
    }

    /**
     * Calcula a média da idade dos alunos com base nas matrículas.
     * @param matriculas lista de matrículas para cálculo
     * @return média das idades ou 0 se lista vazia
     */
    private double calcularMediaIdade(List<Matricula> matriculas) {
        if (matriculas.isEmpty()) return 0.0;

        double somaIdades = matriculas.stream()
                .map(Matricula::getAluno)
                .mapToInt(this::calcularIdade)
                .sum();

        return somaIdades / matriculas.size();
    }

    /**
     * Calcula a idade de um aluno a partir da data de nascimento.
     * @param aluno objeto aluno com data de nascimento
     * @return idade em anos, ou 0 se data de nascimento for nula
     */
    private int calcularIdade(com.escola.model.Aluno aluno) {
        LocalDate nascimento = aluno.getDataNascimento();
        if (nascimento == null) return 0;

        return Period.between(nascimento, LocalDate.now()).getYears();
    }

    /**
     * Conta quantos alunos se matricularam nos últimos X dias.
     * @param matriculas lista de matrículas
     * @param dias período em dias para considerar "novo aluno"
     * @return quantidade de novos alunos
     */
    private long contarNovosAlunosNosUltimosDias(List<Matricula> matriculas, int dias) {
        LocalDate dataLimite = LocalDate.now().minusDays(dias);
        return matriculas.stream()
                .filter(m -> m.getDataMatricula() != null && m.getDataMatricula().isAfter(dataLimite))
                .count();
    }
}
