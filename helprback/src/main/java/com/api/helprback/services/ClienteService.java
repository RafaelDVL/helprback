package com.api.helprback.services;


import com.api.helprback.domain.Cliente;
import com.api.helprback.domain.Pessoa;
import com.api.helprback.domain.dtos.ClienteDTO;
import com.api.helprback.repositories.ClienteRepository;
import com.api.helprback.repositories.PessoaRepository;
import com.api.helprback.services.exceptions.DataIntegrityViolationException;
import com.api.helprback.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository repository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public List<Cliente> findAll(){
        return repository.findAll();
    }

    public Cliente findById(Integer id){
        Optional<Cliente> obj =  repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("O Dado não foi encontrado!: " + id));
    }


    public Cliente create(ClienteDTO objDto){
        objDto.setId(null);
        validaPorCpfEmail(objDto);
        Cliente newObj = new Cliente(objDto);
        return repository.save(newObj);
    }

    private void validaPorCpfEmail(ClienteDTO objDto){
        Optional<Pessoa> obj =  pessoaRepository.findByCpf(objDto.getCpf());
        if (obj.isPresent() && obj.get().getId() != objDto.getId()){
            throw new DataIntegrityViolationException("O CPF ja foi cadastrado !");
        }

        obj = pessoaRepository.findByEmail(objDto.getEmail());
        if (obj.isPresent() && obj.get().getId() != objDto.getId()){
            throw new DataIntegrityViolationException("E-mail ja esta cadastrado no sistema!");
        }

    }

    public Cliente update(Integer id, ClienteDTO objDto){
        objDto.setId(id);
        Cliente oldobj = findById(id);
        validaPorCpfEmail(objDto);
        oldobj = new Cliente(objDto);
        return repository.save(oldobj);
    }

    public void delete(Integer id){
        Cliente obj = findById(id);
        if(obj.getChamados().size() > 0){
            throw new DataIntegrityViolationException("Existe chamados em aberto com o Cliente selecionado, ele não pode ser excluido!");
        }
        repository.deleteById(id);
    }
}