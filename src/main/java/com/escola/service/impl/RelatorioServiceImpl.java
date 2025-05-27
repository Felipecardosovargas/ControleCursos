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
            double mediaIdade = calcularMediaIdadeAlunos(matriculas);
            long novosAlunos = contarNovosAlunosUltimos30Dias(matriculas);

            relatorios.add(criarRelatorioCursoDTO(curso.getNome(), totalMatriculados, mediaIdade, novosAlunos));
        }
        return relatorios;
    }

    private double calcularMediaIdadeAlunos(List<Matricula> matriculas) {
        if (matriculas.isEmpty()) return 0.0;
        double somaIdades = matriculas.stream()
                .mapToLong(m -> Period.between(m.getAluno().getDataNascimento(), LocalDate.now()).getYears())
                .sum();
        return somaIdades / matriculas.size();
    }

    private long contarNovosAlunosUltimos30Dias(List<Matricula> matriculas) {
        LocalDate trintaDiasAtras = LocalDate.now().minusDays(30);
        return matriculas.stream()
                .filter(m -> m.getDataMatricula().isAfter(trintaDiasAtras))
                .count();
    }

    private RelatorioCursoDTO criarRelatorioCursoDTO(String cursoNome, long totalMatriculados, double mediaIdade, long novosAlunos) {
        return new RelatorioCursoDTO(cursoNome, totalMatriculados, mediaIdade, novosAlunos);
    }
}