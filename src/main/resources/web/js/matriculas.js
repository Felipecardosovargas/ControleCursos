window.addEventListener('DOMContentLoaded', () => {
    carregarAlunos();
    carregarCursos();
});

// Carrega alunos no select
async function carregarAlunos() {
    try {
        const res = await fetch('/api/alunos');
        if (!res.ok) throw new Error('Erro ao buscar alunos');

        const alunos = await res.json();
        const select = document.getElementById('selectAluno');
        select.innerHTML = '<option value="" disabled selected>Selecione um aluno</option>';

        alunos.forEach(({ id, nome }) => {
            const opt = document.createElement('option');
            opt.value = id;
            opt.textContent = nome;
            select.appendChild(opt);
        });
    } catch (err) {
        showMessage('formMatriculaMsg', `Erro ao carregar alunos: ${err.message}`, 'error');
    }
}

// Carrega cursos no select
async function carregarCursos() {
    try {
        const res = await fetch('/api/cursos');
        if (!res.ok) throw new Error('Erro ao buscar cursos');

        const cursos = await res.json();
        const select = document.getElementById('selectCurso');
        select.innerHTML = '<option value="" disabled selected>Selecione um curso</option>';

        cursos.forEach(({ id, nome }) => {
            const opt = document.createElement('option');
            opt.value = id;
            opt.textContent = nome;
            select.appendChild(opt);
        });
    } catch (err) {
        showMessage('formMatriculaMsg', `Erro ao carregar cursos: ${err.message}`, 'error');
    }
}

// Submissão do formulário
document.getElementById('cadastroMatriculaForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const alunoId = e.target.selectAluno.value;
    const cursoId = e.target.selectCurso.value;

    if (!alunoId || !cursoId) {
        return showMessage('formMatriculaMsg', 'Selecione aluno e curso para matricular.', 'error');
    }

    try {
        const response = await fetch('/api/matriculas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ alunoId, cursoId }),
        });

        if (!response.ok) {
            const erro = await response.text();
            throw new Error(erro || 'Erro desconhecido ao cadastrar matrícula');
        }

        const data = await response.json(); // Confirma que está recebendo a resposta correta
        console.log('Matrícula criada:', data);

        showMessage('formMatriculaMsg', 'Matrícula cadastrada com sucesso!', 'success');
        e.target.reset();
        listarMatriculas(); // Recarrega a tabela
    } catch (err) {
        showMessage('formMatriculaMsg', `Erro: ${err.message}`, 'error');
    }
});

// Listar matrículas
document.getElementById('btnListarMatriculas').addEventListener('click', listarMatriculas);

async function listarMatriculas() {
    try {
        const res = await fetch('/api/matriculas');
        if (!res.ok) throw new Error('Erro ao buscar matrículas');

        const response = await res.json();

        // Pega o array dentro da propriedade 'data' do objeto retornado
        const matriculas = response.data;

        const tbody = document.querySelector('#tabelaMatriculas tbody');
        tbody.innerHTML = '';

        if (!matriculas || matriculas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">Nenhuma matrícula cadastrada.</td></tr>';
            return;
        }

        matriculas.forEach(({ id, alunoNome, cursoNome, dataMatricula }) => {
            // dataMatricula é um array [ano, mes, dia], monte uma data JS:
            const data = new Date(dataMatricula[0], dataMatricula[1] - 1, dataMatricula[2]);

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${id}</td>
                <td>${alunoNome}</td>
                <td>${cursoNome}</td>
                <td>${data.toLocaleDateString()}</td>
                <td><button class="btn btn-small btn-delete" data-id="${id}">Cancelar</button></td>
            `;
            tbody.appendChild(tr);
        });

        adicionarEventosDeletar();
    } catch (err) {
        showMessage('listaMatriculasMsg', `Erro ao listar matrículas: ${err.message}`, 'error');
    }
}

// Cancelar matrícula
function adicionarEventosDeletar() {
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', async () => {
            const id = btn.dataset.id;
            if (!confirm('Deseja cancelar esta matrícula?')) return;

            try {
                const res = await fetch(`/api/matriculas/${id}`, { method: 'DELETE' });

                if (!res.ok) {
                    const msg = await res.text();
                    throw new Error(msg || 'Erro ao cancelar matrícula');
                }

                showMessage('listaMatriculasMsg', 'Matrícula cancelada com sucesso.', 'success');
                listarMatriculas();
            } catch (err) {
                showMessage('listaMatriculasMsg', `Erro: ${err.message}`, 'error');
            }
        });
    });
}

// Exibir mensagens
function showMessage(elementId, message, type = 'success') {
    const el = document.getElementById(elementId);
    el.textContent = message;
    el.className = `message ${type}`;
}
