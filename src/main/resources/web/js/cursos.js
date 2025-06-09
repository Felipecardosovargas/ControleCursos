document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("cadastroCursoForm");
    const btnListarCursos = document.getElementById("btnListarCursos");
    const tabelaCursosBody = document.querySelector("#tabelaCursos tbody");
    const formCursoMsg = document.getElementById("formCursoMsg");
    const listaCursosMsg = document.getElementById("listaCursosMsg");

    const API_BASE = "/api/cursos";

    function mostrarMensagem(elemento, mensagem, sucesso = true) {
        if (!elemento) return;

        elemento.textContent = mensagem;
        elemento.classList.remove("sucesso", "error");
        elemento.classList.add(sucesso ? "sucesso" : "error");
        elemento.style.display = "flex";

        setTimeout(() => {
            elemento.textContent = "";
            elemento.style.display = "none";
        }, 4000);
    }

    function atualizarTabela(cursos) {
        if (!tabelaCursosBody) return;

        if (!cursos || cursos.length === 0) {
            tabelaCursosBody.innerHTML = `
                <tr><td colspan="4" style="text-align:center;">Nenhum curso cadastrado.</td></tr>
            `;
            return;
        }

        tabelaCursosBody.innerHTML = cursos.map(curso => `
            <tr>
                <td>${curso.id}</td>
                <td>${curso.nome}</td>
                <td>${curso.descricao}</td>
                <td>${curso.cargaHoraria}</td>
            </tr>
        `).join("");
    }

    async function listarCursos() {
        try {
            const response = await fetch(API_BASE);
            if (!response.ok) throw new Error("Erro ao buscar cursos.");
            const cursos = await response.json();
            atualizarTabela(cursos);

            // Exibe mensagem apenas se estiver na página curso-lista.html
            if (window.location.pathname.includes("curso-lista.html")) {
                mostrarMensagem(listaCursosMsg, "Cursos listados com sucesso!");
            }

        } catch (error) {
            mostrarMensagem(listaCursosMsg, error.message, false);
            tabelaCursosBody.innerHTML = "";
        }
    }

    if (form) {
        form.addEventListener("submit", async (event) => {
            event.preventDefault();

            const nome = document.getElementById("nomeCurso")?.value.trim();
            const descricao = document.getElementById("descricaoCurso")?.value.trim();
            const cargaHoraria = parseInt(document.getElementById("cargaHorariaCurso")?.value, 10);

            if (!nome || !descricao || isNaN(cargaHoraria) || cargaHoraria < 1) {
                mostrarMensagem(formCursoMsg, "Por favor, preencha todos os campos corretamente.", false);
                return;
            }

            const novoCurso = { nome, descricao, cargaHoraria };

            try {
                const response = await fetch(API_BASE, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(novoCurso),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || "Erro ao salvar curso.");
                }

                mostrarMensagem(formCursoMsg, "Curso salvo com sucesso!");
                form.reset();
                listarCursos();
            } catch (error) {
                mostrarMensagem(formCursoMsg, error.message, false);
            }
        });
    }

    if (btnListarCursos) {
        btnListarCursos.addEventListener("click", listarCursos);
    }

    listarCursos();
});
