<?php

namespace App\Form;

use App\Entity\Tache;
use App\Entity\Projet;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class TacheType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('libelle', TextType::class, [
                'label' => 'Libellé',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('priorite', ChoiceType::class, [
                'label' => 'Priorité',
                'choices' => [
                    'Basse' => Tache::PRIORITE_BASSE,
                    'Moyenne' => Tache::PRIORITE_MOYENNE,
                    'Haute' => Tache::PRIORITE_HAUTE,
                ],
                'attr' => ['class' => 'form-select'],
            ])
            ->add('dateLimite', DateType::class, [
                'label' => 'Date limite',
                'widget' => 'single_text',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('etat', ChoiceType::class, [
                'label' => 'État',
                'choices' => [
                    'À faire' => Tache::ETAT_A_FAIRE,
                    'En cours' => Tache::ETAT_EN_COURS,
                    'Terminé' => Tache::ETAT_TERMINE,
                ],
                'attr' => ['class' => 'form-select'],
            ])
            ->add('projet', EntityType::class, [
                'label' => 'Projet',
                'class' => Projet::class,
                'choice_label' => 'nom',
                'attr' => ['class' => 'form-select'],
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Tache::class,
        ]);
    }
}
