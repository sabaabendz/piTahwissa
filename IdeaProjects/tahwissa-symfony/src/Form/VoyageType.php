<?php

namespace App\Form;

use App\Entity\Voyage;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\MoneyType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\UrlType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class VoyageType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('titre', TextType::class, [
                'label' => 'Titre du voyage',
                'attr' => [
                    'placeholder' => 'Ex: Séjour à Djerba',
                    'class' => 'form-control',
                ],
            ])
            ->add('description', TextareaType::class, [
                'label' => 'Description',
                'required' => false,
                'attr' => [
                    'placeholder' => 'Décrivez le voyage...',
                    'rows' => 4,
                    'class' => 'form-control',
                ],
            ])
            ->add('destination', TextType::class, [
                'label' => 'Destination',
                'attr' => [
                    'placeholder' => 'Ex: Djerba, Paris, Kenya...',
                    'class' => 'form-control',
                ],
            ])
            ->add('categorie', ChoiceType::class, [
                'label' => 'Catégorie',
                'required' => false,
                'choices' => [
                    'Détente' => 'Détente',
                    'Culturel' => 'Culturel',
                    'Aventure' => 'Aventure',
                    'Romantique' => 'Romantique',
                    'Camping' => 'camping',
                    'Sport' => 'Sport',
                    'Gastronomie' => 'Gastronomie',
                ],
                'placeholder' => 'Choisir une catégorie',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('prixUnitaire', MoneyType::class, [
                'label' => 'Prix unitaire',
                'currency' => 'TND',
                'attr' => [
                    'placeholder' => '0.00',
                    'class' => 'form-control',
                ],
            ])
            ->add('dateDepart', DateType::class, [
                'label' => 'Date de départ',
                'widget' => 'single_text',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('dateRetour', DateType::class, [
                'label' => 'Date de retour',
                'widget' => 'single_text',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('placesDisponibles', IntegerType::class, [
                'label' => 'Places disponibles',
                'attr' => [
                    'min' => 1,
                    'placeholder' => 'Nombre de places',
                    'class' => 'form-control',
                ],
            ])
            ->add('imageUrl', UrlType::class, [
                'label' => 'URL de l\'image',
                'required' => false,
                'default_protocol' => null,
                'attr' => [
                    'placeholder' => 'https://example.com/image.jpg',
                    'class' => 'form-control',
                ],
            ])
            ->add('statut', ChoiceType::class, [
                'label' => 'Statut',
                'choices' => [
                    'Actif' => 'ACTIF',
                    'Inactif' => 'INACTIF',
                    'Complet' => 'COMPLET',
                ],
                'attr' => ['class' => 'form-control'],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Voyage::class,
        ]);
    }
}
