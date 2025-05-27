// Preenche os selects de alunos e cursos ao carregar a página
window.addEventListener('DOMContentLoaded', () => {
    carregarAlunos();
    carregarCursos();
});

// Função para carregar alunos no select
async function carregarAlunos() {
    try {
        const res = await fetch('/api/alunos');
        if (!res.ok) throw new Error('Falha ao carregar alunos');
        const alunos = await res.json();

        const selectAluno = document.getElementById('selectAluno');
        alunos.forEach(aluno => {
            const option = document.createElement('option');
            option.value = aluno.id;
            option.textContent = aluno.nome;
            selectAluno.appendChild(option);
        });
    } catch (err) {
        showMessage('formMatriculaMsg', `Erro ao carregar alunos: ${err.message}`, 'error');
    }
}

// Função para carregar cursos no select
async function carregarCursos() {
    try {
        const res = await fetch('/api/cursos');
        if (!res.ok) throw new Error('Falha ao carregar cursos');
        const cursos = await res.json();

        const selectCurso = document.getElementById('selectCurso');
        cursos.forEach(curso => {
            const option = document.createElement('option');
            option.value = curso.id;
            option.textContent = curso.nome;
            selectCurso.appendChild(option);
        });
    } catch (err) {
        showMessage('formMatriculaMsg', `Erro ao carregar cursos: ${err.message}`, 'error');
    }
}

// Submissão do formulário de matrícula
document.getElementById('cadastroMatriculaForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const alunoId = e.target.selectAluno.value;
    const cursoId = e.target.selectCurso.value;

    if (!alunoId || !cursoId) {
        showMessage('formMatriculaMsg', 'Selecione aluno e curso para matricular.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/matriculas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ alunoId, cursoId }),
        });

        if (response.ok) {
            showMessage('formMatriculaMsg', 'Matrícula cadastrada com sucesso!', 'success');
            e.target.reset();
            listarMatriculas();
        } else {
            const errorMsg = await response.text();
            showMessage('formMatriculaMsg', `Erro: ${errorMsg}`, 'error');
        }
    } catch (err) {
        showMessage('formMatriculaMsg', `Erro ao conectar: ${err.message}`, 'error');
    }
});

// Listar matrículas existentes
document.getElementById('btnListarMatriculas').addEventListener('click', listarMatriculas);

async function listarMatriculas() {
    try {
        const res = await fetch('/api/matriculas');
        if (!res.ok) throw new Error('Falha ao carregar matrículas');

        const matriculas = await res.json();
        const tbody = document.querySelector('#tabelaMatriculas tbody');
        tbody.innerHTML = '';

        if (matriculas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">Nenhuma matrícula cadastrada.</td></tr>';
            return;
        }

        matriculas.forEach(m => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${m.id}</td>
                <td>${m.aluno.nome}</td>
                <td>${m.curso.nome}</td>
                <td>${new Date(m.dataMatricula).toLocaleDateString()}</td>
                <td>
                    <button data-id="${m.id}" class="btn btn-small btn-delete">Cancelar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
        adicionarEventosDeletar();
    } catch (err) {
        showMessage('listaMatriculasMsg', `Erro ao carregar matrículas: ${err.message}`, 'error');
    }
}

// Função para deletar matrícula (exemplo básico)
function adicionarEventosDeletar() {
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const id = e.target.dataset.id;
            if (!confirm('Confirma o cancelamento desta matrícula?')) return;

            try {
                const res = await fetch(`/api/matriculas/${id}`, { method: 'DELETE' });
                if (res.ok) {
                    showMessage('listaMatriculasMsg', 'Matrícula cancelada com sucesso.', 'success');
                    listarMatriculas();
                } else {
                    const error = await res.text();
                    showMessage('listaMatriculasMsg', `Erro ao cancelar matrícula: ${error}`, 'error');
                }
            } catch (err) {
                showMessage('listaMatriculasMsg', `Erro na requisição: ${err.message}`, 'error');
            }
        });
    });
}

// Função para mostrar mensagens reutilizáveis
function showMessage(elementId, message, type = 'success') {
    const el = document.getElementById(elementId);
    el.textContent = message;
    el.className = `message ${type}`;
}
