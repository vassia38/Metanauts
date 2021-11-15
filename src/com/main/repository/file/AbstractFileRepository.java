package com.main.repository.file;

import com.main.model.Entity;
import com.main.model.validators.Validator;
import com.main.repository.memory.InMemoryRepository;

import java.io.*;

import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie = br.readLine()) != null){
                //System.out.println(linie);
                List<String> attribs = Arrays.asList(linie.split(";"));
                E ent = extractEntity(attribs);
                super.save(ent);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes a list of attributes for entities
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);


    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){
       E ent = super.save(entity);
       if(ent == null){
           writeToFile(entity);
       }
       return ent;
    }
    @Override
    public E delete(ID id){
        E ent = super.delete(id);
        if(ent != null){
            this.writeAllToFile();
        }
        return ent;
    }

    protected void writeToFile(E entity){
        try(BufferedWriter br = new BufferedWriter(new FileWriter(fileName, true))){
            br.write(createEntityAsString(entity));
            br.newLine();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    protected void writeAllToFile(){
        try(BufferedWriter br = new BufferedWriter(new FileWriter(fileName, false))){
            for(E entity : this.findAll()){
                br.write(createEntityAsString(entity));
                br.newLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

